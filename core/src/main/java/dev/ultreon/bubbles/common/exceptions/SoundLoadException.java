package dev.ultreon.bubbles.common.exceptions;

public class SoundLoadException extends RuntimeException {
    public SoundLoadException() {
    }

    public SoundLoadException(String message) {
        super(message);
    }

    public SoundLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoundLoadException(Throwable cause) {
        super(cause);
    }
}
