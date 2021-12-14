package fr.lagardedev.orm.exceptions;

public class ClassIsNotAnEntityException extends Exception {

    public ClassIsNotAnEntityException(Class clazz) {
        super("\"" + clazz.getSimpleName() +"\" class is not an Entity class.");
    }

}
