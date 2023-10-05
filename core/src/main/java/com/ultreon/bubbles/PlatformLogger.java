package com.ultreon.bubbles;

import com.badlogic.gdx.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

class PlatformLogger implements ApplicationLogger {
    private final Logger logger = GamePlatform.get().getLogger("LibGDX");

    @Override
    public void log(String tag, String message) {
        Marker marker = MarkerFactory.getMarker(tag);
        this.logger.info(marker, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        Marker marker = MarkerFactory.getMarker(tag);
        this.logger.info(marker, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        Marker marker = MarkerFactory.getMarker(tag);
        this.logger.error(marker, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        Marker marker = MarkerFactory.getMarker(tag);
        this.logger.error(marker, message, exception);
    }

    @Override
    public void debug(String tag, String message) {
        Marker marker = MarkerFactory.getMarker(tag);
        this.logger.debug(marker, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        Marker marker = MarkerFactory.getMarker(tag);
        this.logger.debug(marker, message, exception);
    }
}
