package com.ultreon.bubbles.event.v1.bus;

import com.ultreon.bubbles.event.v1.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Deprecated
public class GameEvents extends AbstractEvents<Event> {
    @Deprecated
    private static final GameEvents instance = new GameEvents(LogManager.getLogger("Game-Events"));

    @Deprecated
    public GameEvents(Logger logger) {
        super(logger);
    }

    @Deprecated
    public static GameEvents get() {
        return instance;
    }
}
