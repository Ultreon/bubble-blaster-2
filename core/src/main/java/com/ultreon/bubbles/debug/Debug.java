package com.ultreon.bubbles.debug;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class Debug {
    private static final Logger logger = LoggerFactory.getLogger("Debugger");

    public static void notify(String name, String detail) {
        BubbleBlaster.getInstance().notifications.notifyPlayer(new Notification(name, detail, "debug"));
    }

    public static void log(String name, String message) {
        if (BubbleBlaster.isDebugMode()) {
            logger.info(MarkerFactory.getMarker(name), message);
        }
    }
}
