package com.ultreon.commons.utilities.python.builtins;

/**
 * @deprecated use {@link ArithmeticException} instead (see javadoc)
 */
@Deprecated
public class ZeroDivisionError extends RuntimeException {
    public ZeroDivisionError() {
    }

    public ZeroDivisionError(String message) {
        super(message);
    }

    public ZeroDivisionError(String message, Throwable cause) {
        super(message, cause);
    }

    public ZeroDivisionError(Throwable cause) {
        super(cause);
    }

    public ZeroDivisionError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
