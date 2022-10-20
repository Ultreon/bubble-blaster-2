package com.ultreon.commons.utilities.python.builtins;

@Deprecated
public class RuntimeError extends RuntimeException {
    public RuntimeError() {
    }

    public RuntimeError(String message) {
        super(message);
    }

    public RuntimeError(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeError(Throwable cause) {
        super(cause);
    }
}
