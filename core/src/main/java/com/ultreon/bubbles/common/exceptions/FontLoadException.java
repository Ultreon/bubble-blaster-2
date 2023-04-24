package com.ultreon.bubbles.common.exceptions;

import java.awt.*;

public class FontLoadException extends RuntimeException {
    public FontLoadException() {
        super();
    }

    public FontLoadException(String message) {
        super(message);
    }

    public FontLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FontLoadException(Throwable cause) {
        super(cause);
    }
}
