package com.ultreon.bubbles.common.logging;

import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(since = "0.0.2995-indev5", forRemoval = true)
public final class GameLogRecord {
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final long nanoTime;
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final long milliTime;
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final String message;
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final GameLogger logger;
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final GameLogLevel level;

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public GameLogRecord(String message, GameLogger logger, @NonNull GameLogLevel level) {
        this.logger = logger;
        this.nanoTime = System.nanoTime();
        this.milliTime = System.currentTimeMillis();
        this.message = message;
        this.level = level;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public long getNanoTime() {
        return nanoTime;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public long getMilliTime() {
        return milliTime;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public String getMessage() {
        return message;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public GameLogger getLogger() {
        return logger;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public String getLoggerName() {
        return getLogger().getName();
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public GameLogLevel getLevel() {
        return level;
    }
}
