package com.ultreon.bubbles.debug;

import com.ultreon.bubbles.BubbleBlaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Debugger {
    private static final Logger logger = LogManager.getLogger("Debugger");

    public static void log(String message) {
        if (BubbleBlaster.isDebugMode()) {
            logger.info(message);
        }
    }
}
