package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.event.v1.utils.FontLoader;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.GameWindow;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.libs.crash.v0.ApplicationCrash;
import com.ultreon.libs.events.v1.Event;
import com.ultreon.libs.events.v1.EventResult;

import java.util.Locale;

public class GameEvents {
    public static final Event<LanguageChanged> LANGUAGE_CHANGED = Event.create();
    public static final Event<Crash> CRASH = Event.create();
    @Deprecated public static final Event<AutoRegister> AUTO_REGISTER = Event.create();
    public static final Event<CollectTextures> COLLECT_TEXTURES = Event.create();
    @Deprecated public static final Event<RegistryDump> REGISTRY_DUMP = Event.create();
    public static final Event<LoadFonts> LOAD_FONTS = Event.create();
    public static final Event<ClientStarted> CLIENT_STARTED = Event.create();

    @FunctionalInterface
    public interface LanguageChanged {
        void onLanguageChanged(Locale from, Locale to);
    }

    @FunctionalInterface
    public interface Crash {
        void onCrash(ApplicationCrash crash);
    }

    @Deprecated
    @FunctionalInterface
    public interface AutoRegister {
        @Deprecated
        void onAutoRegister(Registry<?> registry);
    }

    @FunctionalInterface
    public interface CollectTextures {
        void onCollectTextures(TextureCollection collection);
    }

    @FunctionalInterface
    public interface RegistryDump {
        void onDump();
    }

    @FunctionalInterface
    public interface LoadFonts {
        void onLoadFonts(FontLoader loader);
    }

    @FunctionalInterface
    public interface ClientStarted {
        void onClientStarted(BubbleBlaster loader);
    }
}
