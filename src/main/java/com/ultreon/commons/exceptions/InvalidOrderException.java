package com.ultreon.commons.exceptions;

public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException() {
        super();
    }

    public InvalidOrderException(String message) {
        super(message);
    }

    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOrderException(Throwable cause) {
        super(cause);
    }

    public InvalidOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
