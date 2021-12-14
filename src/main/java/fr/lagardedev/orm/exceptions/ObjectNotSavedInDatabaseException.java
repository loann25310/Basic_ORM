package fr.lagardedev.orm.exceptions;

public class ObjectNotSavedInDatabaseException extends Exception {

    public ObjectNotSavedInDatabaseException(Object o) {
        super("The given object is not saved into the database.");
    }

}
