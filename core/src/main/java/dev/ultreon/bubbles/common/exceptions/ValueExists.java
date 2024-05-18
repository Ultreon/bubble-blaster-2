package dev.ultreon.bubbles.common.exceptions;

import dev.ultreon.libs.collections.v0.exceptions.ValueExistsException;

/**
 * @deprecated use {@link ValueExistsException} from CoreLibs instead.
 */
@Deprecated
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
