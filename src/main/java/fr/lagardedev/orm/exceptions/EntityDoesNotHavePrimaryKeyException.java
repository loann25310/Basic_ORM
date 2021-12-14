package fr.lagardedev.orm.exceptions;

public class EntityDoesNotHavePrimaryKeyException extends Exception {

    public EntityDoesNotHavePrimaryKeyException(Class entity) {
        super("The \"" + entity.getSimpleName() + "\" entity does not have a primary key.");
    }

}
