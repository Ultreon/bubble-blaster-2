package com.ultreon.preloader;

import net.fabricmc.loader.impl.util.log.LogCategory;
import net.fabricmc.loader.impl.util.log.LogHandler;
import net.fabricmc.loader.impl.util.log.LogLevel;
import org.apache.logging.log4j.*;

import java.util.HashMap;
import java.util.Map;

public class BB2LogHandler implements LogHandler {
    private static final Logger LOGGER = LogManager.getLogger("FabricLoader");
    private final Map<LogCategory, Marker> markerMap = new HashMap<>();

    @Override
    public void log(long time, LogLevel level, LogCategory category, String msg, Throwable exc, boolean fromReplay, boolean wasSuppressed) {
        Marker marker = markerMap.computeIfAbsent(category, logCategory -> MarkerManager.getMarker(logCategory.name));
        LOGGER.log(getLevel(level), marker, msg, exc);
    }

    @Override
    public boolean shouldLog(LogLevel level, LogCategory category) {
        return LOGGER.isEnabled(getLevel(level));
    }

    private static Level getLevel(LogLevel level) {
        return switch (level) {
            case INFO -> Level.INFO;
            case WARN -> Level.WARN;
            case DEBUG -> Level.DEBUG;
            case ERROR -> Level.ERROR;
            case TRACE -> Level.TRACE;
        };
    }

    @Override
    public void close() {

    }
}
