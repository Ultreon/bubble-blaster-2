package dev.ultreon.bubbles.cli;

public class ANSI {
    private ANSI() {

    }

    public static final String START_CODE = "^<ESC^>";

    public static final String ANSI_RESET = START_CODE + "[0m";
    public static final String ANSI_BLACK = START_CODE + "[30m";
    public static final String ANSI_RED = START_CODE + "[31m";
    public static final String ANSI_GREEN = START_CODE + "[32m";
    public static final String ANSI_YELLOW = START_CODE + "[33m";
    public static final String ANSI_BLUE = START_CODE + "[34m";
    public static final String ANSI_PURPLE = START_CODE + "[35m";
    public static final String ANSI_CYAN = START_CODE + "[36m";
    public static final String ANSI_WHITE = START_CODE + "[37m";
    public static final String ANSI_BRIGHT_BLACK = START_CODE + "[90m";
    public static final String ANSI_BRIGHT_RED = START_CODE + "[91m";
    public static final String ANSI_BRIGHT_GREEN = START_CODE + "[92m";
    public static final String ANSI_BRIGHT_YELLOW = START_CODE + "[93m";
    public static final String ANSI_BRIGHT_BLUE = START_CODE + "[94m";
    public static final String ANSI_BRIGHT_PURPLE = START_CODE + "[95m";
    public static final String ANSI_BRIGHT_CYAN = START_CODE + "[96m";
    public static final String ANSI_BRIGHT_WHITE = START_CODE + "[97m";
}
