package com.ultreon.bubbles.common.logging.old;

import java.util.logging.Level;

@Deprecated(since = "0.0.2995-indev5", forRemoval = true)
public class GameLoggingLevels extends Level {
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static final Level DEBUG = new GameLoggingLevels("DEBUG", 1);

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    @SuppressWarnings("SameParameterValue")
    protected GameLoggingLevels(String name, int value) {
        super(name, value);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    protected GameLoggingLevels(String name, int value, String resourceBundleName) {
        super(name, value, resourceBundleName);
    }
}
