package com.ultreon.bubbles.event.v2;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.GameWindow;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.commons.crash.ApplicationCrash;

import java.util.Locale;

public class GameEvents {
    public static final Event<GameExit> GAME_EXIT = Event.create();
    public static final Event<LanguageChanged> LANGUAGE_CHANGED = Event.create();
    public static final Event<Crash> CRASH = Event.create();
    public static final Event<AutoRegister> AUTO_REGISTER = Event.create();
    public static final Event<CollectTextures> COLLECT_TEXTURES = Event.create();
    public static final Event<WindowClosing> WINDOW_CLOSING = Event.withResult();
    public static final Event<RegistryDump> REGISTRY_DUMP = Event.create();

    @FunctionalInterface
    public interface GameExit {
        void onExit(BubbleBlaster game);
    }

    @FunctionalInterface
    public interface LanguageChanged {
        void onLanguageChanged(Locale from, Locale to);
    }

    @FunctionalInterface
    public interface Crash {
        void onCrash(ApplicationCrash crash);
    }

    @FunctionalInterface
    public interface AutoRegister {
        void onAutoRegister(Registry<?> registry);
    }

    @FunctionalInterface
    public interface CollectTextures {
        void onCollectTextures(TextureCollection collection);
    }

    @FunctionalInterface
    public interface WindowClosing {
        EventResult<Boolean> onWindowClosing(GameWindow window);
    }

    @FunctionalInterface
    public interface RegistryDump {
        void onDump();
    }
}
