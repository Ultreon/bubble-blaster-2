package com.ultreon.commons.utilities.python.builtins;

@Deprecated
public class OSError extends RuntimeException {
    public OSError() {
    }

    public OSError(String message) {
        super(message);
    }

    public OSError(String message, Throwable cause) {
        super(message, cause);
    }

    public OSError(Throwable cause) {
        super(cause);
    }
}
