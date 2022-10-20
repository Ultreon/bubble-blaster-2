package com.ultreon.commons.utilities.python.builtins;

@Deprecated
public class FileNotFoundError extends RuntimeException {
    public FileNotFoundError() {
    }

    public FileNotFoundError(String message) {
        super(message);
    }

    public FileNotFoundError(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotFoundError(Throwable cause) {
        super(cause);
    }

    public FileNotFoundError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
