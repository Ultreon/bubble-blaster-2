package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.libs.events.v1.Event;

public class LifecycleEvents {
    public static final Event<Setup> SETUP = Event.create();
    public static final Event<GameExit> GAME_EXIT = Event.create();

    @FunctionalInterface
    public interface Setup {
        void onSetup(BubbleBlaster game);
    }

    @FunctionalInterface
    public interface GameExit {
        void onExit(BubbleBlaster game);
    }
}
