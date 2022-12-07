package com.ultreon.bubbles.common.exceptions;

public class ValueExists extends Throwable {
    public ValueExists() {
    }

    public ValueExists(String message) {
        super(message);
    }

    public ValueExists(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueExists(Throwable cause) {
        super(cause);
    }

    public ValueExists(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
