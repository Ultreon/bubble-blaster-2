package com.ultreon.bubbles.common.logging;

import org.fusesource.jansi.Ansi;

import java.io.PrintStream;

@Deprecated(since = "0.0.2995-indev5", forRemoval = true)
public enum GameLogLevel {
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    DEBUG("debug", System.err, Ansi.Color.CYAN),
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    INFO("info", System.out, Ansi.Color.WHITE),
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    SUCCESS("success", System.out, Ansi.Color.GREEN),
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    WARN("warn", System.err, Ansi.Color.YELLOW),
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    ERROR("error", System.err, Ansi.Color.RED),
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    FATAL("fatal", System.err, Ansi.Color.RED);

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final String name;
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final PrintStream stream;
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final Ansi.Color ansiColor;

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    GameLogLevel(String name, PrintStream stream, Ansi.Color ansiColor) {
        this.name = name;
        this.stream = stream;
        this.ansiColor = ansiColor;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public String getName() {
        return name;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public PrintStream getStream() {
        return stream;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public Ansi.Color getAnsiColor() {
        return ansiColor;
    }
}
