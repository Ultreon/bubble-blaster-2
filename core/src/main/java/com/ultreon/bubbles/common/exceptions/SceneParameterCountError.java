package com.ultreon.bubbles.common.exceptions;

@Deprecated
public class SceneParameterCountError extends RuntimeException {
    @Deprecated
    private static String getMessage(int needed, int got) {
        String a;
        if (needed == 1 || needed == -1) {
            a = "parameter";
        } else {
            a = "parameters";
        }

        return "Needed " + needed + " " + a + " got " + got + ".";
    }

    @Deprecated
    public SceneParameterCountError(int needed, int got) {
        super(SceneParameterCountError.getMessage(needed, got));
    }
}
