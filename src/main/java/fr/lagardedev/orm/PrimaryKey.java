package fr.lagardedev.orm;

import java.lang.reflect.Type;

public class PrimaryKey {

    private Type type;

    private String fieldName;

    public PrimaryKey() {}

    public PrimaryKey(Type type, String fieldName){
        this.type = type;
        this.fieldName = fieldName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
