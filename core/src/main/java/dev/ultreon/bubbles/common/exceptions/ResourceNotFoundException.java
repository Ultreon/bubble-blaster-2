package dev.ultreon.bubbles.common.exceptions;

import dev.ultreon.libs.commons.v0.Identifier;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

    public ResourceNotFoundException(Identifier langLoc) {
        this(langLoc.toString());
    }
}
