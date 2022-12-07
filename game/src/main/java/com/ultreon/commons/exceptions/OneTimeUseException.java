package com.ultreon.commons.exceptions;

public class OneTimeUseException extends IllegalStateException {
    public OneTimeUseException() {
        super();
    }

    public OneTimeUseException(String s) {
        super(s);
    }

    public OneTimeUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public OneTimeUseException(Throwable cause) {
        super(cause);
    }
}
