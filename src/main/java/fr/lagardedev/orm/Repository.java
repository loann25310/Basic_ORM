package fr.lagardedev.orm;

import fr.lagardedev.orm.annotations.Column;
import fr.lagardedev.orm.annotations.Entity;
import fr.lagardedev.orm.annotations.ManyToOne;
import fr.lagardedev.orm.annotations.Primary;
import fr.lagardedev.orm.exceptions.ClassIsNotAnEntityException;
import fr.lagardedev.orm.exceptions.EntityDoesNotHavePrimaryKeyException;
import fr.lagardedev.orm.exceptions.ObjectNotSavedInDatabaseException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Loann LAGARDE
 * @version 1.0.0
 * @param <T> Type of the repository
 */
public class Repository<T> {

    private final Class<T> clazz;
    private final Connection connection;

    public Repository(Class<T> clazz, Connection connection) throws ClassIsNotAnEntityException {
        this.clazz = clazz;
        if(clazz.getAnnotation(Entity.class) == null) throw new ClassIsNotAnEntityException(clazz);
        this.connection = connection;
    }

    private java.sql.Connection getConnection() {
        return connection.getConn();
    }

    /**
     * Find object by the primary key
     * @param id Value of the primary key
     * @return Instance of specified Type from the database
     */
    public T find(int id) {
        T output;
        try {
            output = clazz.getDeclaredConstructor().newInstance();
            String fieldName = null;
            for (Field f : output.getClass().getFields()) {
                if(f.getAnnotation(Primary.class) != null){
                    fieldName = f.getName();
                    break;
                }
            }
            if(fieldName == null) return null;

            String tableName = clazz.getSimpleName().toLowerCase();
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "SELECT * FROM " + tableName + " WHERE " + fieldName + " = ?;"
            );
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getResultSet();
            resultSet.next();

            for (Field f : output.getClass().getFields()) {
                if(f.getAnnotation(Column.class) != null) assignSimpleValue(f, resultSet, output);
                else if(f.getAnnotation(ManyToOne.class) != null) assignManyToOneValue(f, resultSet, output);

            }
        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException | SQLException | ClassIsNotAnEntityException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        return output;
    }

    /**
     * Update or Insert the object into database
     * @param o Object need to saved
     * @throws NoSuchFieldException
     * @throws EntityDoesNotHavePrimaryKeyException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public void save(Object o) throws NoSuchFieldException, EntityDoesNotHavePrimaryKeyException, IllegalAccessException, SQLException {
        String primaryColumnName = getPrimaryColumn(o);
        StringBuilder query;

        if(primaryColumnName == null) throw new EntityDoesNotHavePrimaryKeyException(o.getClass());
        boolean insert = o.getClass().getField(primaryColumnName).getInt(o) == 0;
        List<String> parameters = new ArrayList<>();

        if(insert){
            query = new StringBuilder("INSERT INTO " + o.getClass().getSimpleName().toLowerCase() + " (");
            for (Field f : o.getClass().getFields()) {
                if(f.getName().equals(primaryColumnName)) continue;
                if(o.getClass().getField(f.getName()).getAnnotation(Column.class) != null){
                    query.append(f.getName()).append(",");
                    parameters.add(f.getName());
                } else if(o.getClass().getField(f.getName()).getAnnotation(ManyToOne.class) != null){
                    query.append(f.getAnnotation(ManyToOne.class).column()).append(",");
                    parameters.add(f.getName());
                }
            }
            if(query.charAt(query.length() - 1) == ',')
                query.deleteCharAt(query.length() - 1);
            query.append(")").append(" VALUES ").append("(").append("?,".repeat(parameters.size()));
            if(query.charAt(query.length() - 1) == ',')
                query.deleteCharAt(query.length() - 1);
            query.append(");");
        } else {
            query = new StringBuilder("UPDATE " + o.getClass().getSimpleName().toLowerCase() + " SET");
            for (Field f : o.getClass().getFields()) {
                if(f.getName().equals(primaryColumnName)) continue;
                if(o.getClass().getField(f.getName()).getAnnotation(Column.class) != null){
                    query.append(" ").append(f.getName()).append(" = ?,");
                    parameters.add(f.getName());
                }else if(o.getClass().getField(f.getName()).getAnnotation(ManyToOne.class) != null){
                    query.append(" ").append(f.getAnnotation(ManyToOne.class).column()).append(" = ?,");
                    parameters.add(f.getName());
                }
            }
            if(query.charAt(query.length() - 1) == ',')
                query.deleteCharAt(query.length() - 1);
            query.append(" WHERE ").append(primaryColumnName).append(" = ?;");
        }

        PreparedStatement preparedStatement = getConnection().prepareStatement(query.toString());

        int i = 0;
        for (String fieldName : parameters) {
            i++;
            if(o.getClass().getField(fieldName).getType() == int.class)
                preparedStatement.setInt(i, o.getClass().getField(fieldName).getInt(o));
            else if(o.getClass().getField(fieldName).getType() == String.class)
                preparedStatement.setString(i, o.getClass().getField(fieldName).get(o).toString());
            else if(o.getClass().getField(fieldName).getType() == boolean.class)
                preparedStatement.setInt(i, o.getClass().getField(fieldName).getBoolean(o) ? 1 : 0);
            else if(o.getClass().getField(fieldName).getAnnotation(ManyToOne.class) != null) {
                Object o2 = o.getClass().getField(fieldName).get(o);
                int o2Id = o2.getClass().getField(getPrimaryColumn(o2)).getInt(o2);
                preparedStatement.setInt(i, o2Id);
            }
        }

        if(!insert)
            preparedStatement.setInt(parameters.size() + 1, o.getClass().getField(primaryColumnName).getInt(o));

        preparedStatement.executeUpdate();

        if(!insert) return;

        preparedStatement = getConnection().prepareStatement("SELECT " + primaryColumnName + " FROM " + o.getClass().getSimpleName().toLowerCase() + " ORDER BY " + primaryColumnName + " DESC LIMIT 1;");
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        resultSet.next();
        o.getClass().getField(primaryColumnName).set(o, resultSet.getInt(primaryColumnName));
    }

    public void delete(Object o) throws NoSuchFieldException, ObjectNotSavedInDatabaseException, IllegalAccessException, EntityDoesNotHavePrimaryKeyException, SQLException {
        String primaryColumnName = getPrimaryColumn(o);
        if(primaryColumnName == null)
            throw new EntityDoesNotHavePrimaryKeyException(o.getClass());
        int id = o.getClass().getField(primaryColumnName).getInt(o);
        if(id <= 0)
            throw new ObjectNotSavedInDatabaseException(o);
        PreparedStatement preparedStatement = getConnection().prepareStatement(
                "DELETE FROM " + o.getClass().getSimpleName().toLowerCase() + " WHERE " + primaryColumnName + " = ?;"
        );
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        o.getClass().getField(primaryColumnName).setInt(o, 0);
    }

    private String getPrimaryColumn(Object o) throws NoSuchFieldException {
        for (Field f : o.getClass().getFields()) {
            if(o.getClass().getField(f.getName()).getAnnotation(Primary.class) != null){
                return f.getName();
            }
        }
        return null;
    }

    private List<PrimaryKey> getPrimaryColumns(Object o) throws NoSuchFieldException {
        List<PrimaryKey> result = new ArrayList<>();
        for (Field f : o.getClass().getFields()) {
            if(o.getClass().getField(f.getName()).getAnnotation(Primary.class) != null)
                result.add(new PrimaryKey(o.getClass().getField(f.getName()).getType(), f.getName()));
        }
        return result;
    }

    private void assignSimpleValue(Field f, ResultSet resultSet, T output) throws NoSuchFieldException, SQLException, IllegalAccessException {
        if(f.getType() == int.class){
            output.getClass().getField(f.getName()).set(output, resultSet.getInt(f.getName()));
        }else if(f.getType() == String.class){
            output.getClass().getField(f.getName()).set(output, resultSet.getString(f.getName()));
        }else if(f.getType() == boolean.class){
            output.getClass().getField(f.getName()).set(output, resultSet.getInt(f.getName()) == 1);
        }
    }

    private void assignManyToOneValue(Field f, ResultSet resultSet, T output) throws InstantiationException, IllegalAccessException, SQLException, NoSuchFieldException, ClassIsNotAnEntityException {
        Object o = (new Repository<>(f.getType(), connection)).find(resultSet.getInt(f.getAnnotation(ManyToOne.class).column()));
        output.getClass().getField(f.getName()).set(output, o);
    }

}
