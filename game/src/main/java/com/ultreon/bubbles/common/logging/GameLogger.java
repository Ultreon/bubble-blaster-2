package com.ultreon.bubbles.common.logging;

import com.ultreon.bubbles.common.References;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Deprecated(since = "0.0.2995-indev5", forRemoval = true)
public class GameLogger {
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final Logger logger;
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private static final ConcurrentHashMap<String, GameLogger> loggers = new ConcurrentHashMap<>();
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private final GameLogFormatter formatter = new GameLogFormatter();
    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private String name;

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void debug(String msg) {
        this.log(GameLogLevel.DEBUG, msg);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void info(String msg) {
        this.log(GameLogLevel.INFO, msg);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void success(String msg) {
        this.log(GameLogLevel.SUCCESS, msg);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void error(String msg) {
        this.log(GameLogLevel.ERROR, msg);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void warning(String msg) {
        this.log(GameLogLevel.WARN, msg);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void fatal(String msg) {
        this.log(GameLogLevel.FATAL, msg);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private void log(GameLogLevel level, String msg) {
        GameLogRecord record = new GameLogRecord(msg, this, level);
        this.formatter.publish(record, printer);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    private static PrintWriter printer = null;

    static {
        try {
            Date date = new Date(System.currentTimeMillis());
            DateFormat formatter = new SimpleDateFormat("dd_MM_yyyy HH_mm_ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String logFileName = formatter.format(date) + ".log";

            FileOutputStream output = new FileOutputStream(new File(References.LOGS_DIR, logFileName), true);
            printer = new PrintWriter(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public GameLogger(String name) throws InstanceAlreadyExistsException {
        if (loggers.containsKey(name))
            throw new InstanceAlreadyExistsException("GameLogger instance with name '" + name + "' already created");

        // Get Java logger.
        this.logger = Logger.getLogger(name);
        this.name = name;

        // Register logger.
        loggers.put(name, this);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static ConcurrentHashMap<String, GameLogger> getLoggers() {
        return loggers;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public static GameLogger getLoggerInstance(String name) {
        return loggers.get(name);
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public String getName() {
        return name;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public void setName(String name) {
        this.name = name;
    }

    @Deprecated(since = "0.0.2995-indev5", forRemoval = true)
    public Logger getLogger() {
        return logger;
    }
}
