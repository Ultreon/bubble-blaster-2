package com.ultreon.bubbles.debug;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.UUID;

public class Debug {
    private static final Logger logger = LoggerFactory.getLogger("Debugger");

    public static void notify(String name, String detail) {
        BubbleBlaster game = BubbleBlaster.getInstance();
        game.notifications.notify(Notification.builder(name, detail).subText("Debug").build());
    }

    public static void notifyOnce(UUID uuid, String name, String detail) {
        BubbleBlaster game = BubbleBlaster.getInstance();
        game.notifications.notifyOnce(uuid, Notification.builder(name, detail).subText("debug").build());
    }

    public static void log(String name, String message) {
//        if (BubbleBlaster.isDebugMode()) {
            logger.info(MarkerFactory.getMarker(name), message);
//        }
    }
}
