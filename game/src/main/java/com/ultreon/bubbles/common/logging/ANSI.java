package com.ultreon.bubbles.common.logging;

@Deprecated(since = "0.0.2995-indev5", forRemoval = true)
public class ANSI {
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private ANSI() {

    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String START_CODE = "^<ESC^>";

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_RESET = START_CODE + "[0m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BLACK = START_CODE + "[30m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_RED = START_CODE + "[31m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_GREEN = START_CODE + "[32m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_YELLOW = START_CODE + "[33m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BLUE = START_CODE + "[34m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_PURPLE = START_CODE + "[35m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_CYAN = START_CODE + "[36m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_WHITE = START_CODE + "[37m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BRIGHT_BLACK = START_CODE + "[90m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BRIGHT_RED = START_CODE + "[91m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BRIGHT_GREEN = START_CODE + "[92m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BRIGHT_YELLOW = START_CODE + "[93m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BRIGHT_BLUE = START_CODE + "[94m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BRIGHT_PURPLE = START_CODE + "[95m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BRIGHT_CYAN = START_CODE + "[96m";
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final String ANSI_BRIGHT_WHITE = START_CODE + "[97m";
}
