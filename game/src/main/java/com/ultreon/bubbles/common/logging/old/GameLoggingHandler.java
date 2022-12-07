package com.ultreon.bubbles.common.logging.old;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@Deprecated(since = "0.0.2995-indev5", forRemoval = true)
public class GameLoggingHandler extends Handler {
    @Override
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void publish(LogRecord record) {
        Date date = new Date(record.getMillis());
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS XXX");
        formatter.setTimeZone(TimeZone.getDefault());
        String dateFormatted = formatter.format(date);

        if (record.getLevel() == GameLoggingLevels.DEBUG || record.getLevel() == Level.WARNING || record.getLevel() == Level.SEVERE) {
            System.err.println(dateFormatted + " | " + record.getLoggerName() + " | " + record.getLevel().getName() + ": " + record.getMessage());
        } else {
            System.out.println(dateFormatted + " | " + record.getLoggerName() + " | " + record.getLevel().getName() + ": " + record.getMessage());
        }
    }

    @Override
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void flush() {
        System.out.flush();
    }

    @Override
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void close() throws SecurityException {
        System.out.close();
    }
}
