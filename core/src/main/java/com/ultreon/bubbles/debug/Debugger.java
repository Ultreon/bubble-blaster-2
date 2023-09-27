package com.ultreon.bubbles.debug;

import com.ultreon.bubbles.BubbleBlaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Deprecated
public class Debugger {
    @Deprecated
    private static final Logger logger = LogManager.getLogger("Debugger");

    @Deprecated
    public static void log(String message) {
        if (BubbleBlaster.isDebugMode()) {
            logger.info(message);
        }
    }
}
