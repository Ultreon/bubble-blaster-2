package com.ultreon.commons.exceptions;

public class InvalidPlatformException extends RuntimeException {
    public InvalidPlatformException() {
        super();
    }

    public InvalidPlatformException(String message) {
        super(message);
    }

    public InvalidPlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPlatformException(Throwable cause) {
        super(cause);
    }
}
