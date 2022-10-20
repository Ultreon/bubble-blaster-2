package com.ultreon.commons.utilities.python.builtins;

@Deprecated
public class FileExistsError extends RuntimeException {
    public FileExistsError() {
    }

    public FileExistsError(String message) {
        super(message);
    }

    public FileExistsError(String message, Throwable cause) {
        super(message, cause);
    }

    public FileExistsError(Throwable cause) {
        super(cause);
    }

    public FileExistsError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
