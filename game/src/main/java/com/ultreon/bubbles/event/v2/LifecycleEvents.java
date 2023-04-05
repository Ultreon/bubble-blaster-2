package com.ultreon.bubbles.event.v2;

import com.ultreon.bubbles.game.BubbleBlaster;

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
