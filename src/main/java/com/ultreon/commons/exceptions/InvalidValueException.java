package com.ultreon.commons.exceptions;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException() {
        super();
    }

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidValueException(Throwable cause) {
        super(cause);
    }

}
