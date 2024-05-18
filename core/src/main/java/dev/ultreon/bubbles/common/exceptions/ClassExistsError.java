package dev.ultreon.bubbles.common.exceptions;

public class ClassExistsError extends Exception {
    public ClassExistsError(String errorMessage) {
        super(errorMessage);
    }
}
