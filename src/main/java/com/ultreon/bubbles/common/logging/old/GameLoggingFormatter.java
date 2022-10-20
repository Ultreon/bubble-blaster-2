package com.ultreon.bubbles.common.logging.old;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

@Deprecated(since = "0.0.2995-indev5", forRemoval = true)
public class GameLoggingFormatter extends Formatter {
    @Override
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public String format(LogRecord record) {
        return "[" + record.getLoggerName() + "] " + record.getLevel().getName() + ": " + record.getMessage() + "\n";
    }
}
