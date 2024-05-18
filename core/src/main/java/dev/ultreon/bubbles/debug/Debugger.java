package dev.ultreon.bubbles.debug;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.GamePlatform;
import org.slf4j.Logger;

@Deprecated
public class Debugger {
    @Deprecated
    private static final Logger logger = GamePlatform.get().getLogger("Debugger");

    @Deprecated
    public static void log(String message) {
        if (BubbleBlaster.isDebugMode()) {
            logger.info(message);
        }
    }
}
