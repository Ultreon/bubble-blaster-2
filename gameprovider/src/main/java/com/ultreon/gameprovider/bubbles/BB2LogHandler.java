package com.ultreon.gameprovider.bubbles;

import org.quiltmc.loader.impl.util.log.LogCategory;
import org.quiltmc.loader.impl.util.log.LogHandler;
import org.quiltmc.loader.impl.util.log.LogLevel;
import org.apache.logging.log4j.*;

import java.util.HashMap;
import java.util.Map;

public class BB2LogHandler implements LogHandler {
    private static final Logger LOGGER = LogManager.getLogger("FabricLoader");
    private final Map<LogCategory, Marker> markerMap = new HashMap<>();

    @Override
    public void log(long time, LogLevel level, LogCategory category, String msg, Throwable exc, boolean fromReplay, boolean wasSuppressed) {
        var marker = this.markerMap.computeIfAbsent(category, logCategory -> MarkerManager.getMarker(logCategory.name));
        BB2LogHandler.LOGGER.log(BB2LogHandler.getLevel(level), marker, msg, exc);
    }

    @Override
    public boolean shouldLog(LogLevel level, LogCategory category) {
        return BB2LogHandler.LOGGER.isEnabled(BB2LogHandler.getLevel(level));
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
