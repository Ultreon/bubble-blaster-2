package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.gui.screen.LoadScreen;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.events.v1.Event;

import java.util.function.Consumer;

public class LifecycleEvents {
    public static final Event<Setup> SETUP = Event.create();
    public static final Event<Finished> FINISHED = Event.create();
    public static final Event<Loaded> LOADED = Event.create();
    public static final Event<Loading> LOADING = Event.create();
    public static final Event<GameExit> GAME_EXIT = Event.create();
    public static final Event<RegisterLanguages> REGISTER_LANGUAGES = Event.create();

    @FunctionalInterface
    public interface Setup {
        void onSetup(BubbleBlaster game);
    }

    @FunctionalInterface
    public interface Loaded {
        void onLoaded(BubbleBlaster game, LoadScreen loadScreen);
    }

    @FunctionalInterface
    public interface Loading {
        void onLoading(BubbleBlaster game, LoadScreen loadScreen);
    }

    @FunctionalInterface
    public interface RegisterLanguages {
        void onRegisterLanguages(BubbleBlaster game, LoadScreen loadScreen, Consumer<Identifier> languageAdder);
    }

    @FunctionalInterface
    public interface Finished {
        void onFinished(BubbleBlaster game);
    }

    @FunctionalInterface
    public interface GameExit {
        void onExit(BubbleBlaster game);
    }
}
