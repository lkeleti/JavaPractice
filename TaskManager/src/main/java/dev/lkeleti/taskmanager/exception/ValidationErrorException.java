package dev.lkeleti.taskmanager.exception;

public class ValidationErrorException extends RuntimeException {

    private final String field;

    public ValidationErrorException(String field, String message) {
        super(message);
        this.field = field;
    }

    public ValidationErrorException(String message) {
        super(message);
        this.field = null;
    }

    public String getField() {
        return field;
    }
}
