package com.ultreon.bubbles.common.exceptions;

public class SceneParameterCountError extends RuntimeException {
    private static String getMessage(int needed, int got) {
        String a;
        if (needed == 1 || needed == -1) {
            a = "parameter";
        } else {
            a = "parameters";
        }

        return "Needed " + needed + " " + a + " got " + got + ".";
    }

    public SceneParameterCountError(int needed, int got) {
        super(getMessage(needed, got));
    }
}
