package com.ultreon.commons.utilities.python.builtins;

@Deprecated
public class PlatformError extends RuntimeException {
    public PlatformError() {
    }

    public PlatformError(String message) {
        super(message);
    }

    public PlatformError(String message, Throwable cause) {
        super(message, cause);
    }

    public PlatformError(Throwable cause) {
        super(cause);
    }
}
