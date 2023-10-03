package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.libs.crash.v0.ApplicationCrash;
import com.ultreon.libs.events.v1.Event;

import java.util.Locale;

public class GameEvents {
    public static final Event<LanguageChanged> LANGUAGE_CHANGED = Event.create();
    public static final Event<Crash> CRASH = Event.create();
    public static final Event<CollectTextures> COLLECT_TEXTURES = Event.create();
    public static final Event<ClientStarted> CLIENT_STARTED = Event.create();

    @FunctionalInterface
    public interface LanguageChanged {
        void onLanguageChanged(Locale from, Locale to);
    }

    @FunctionalInterface
    public interface Crash {
        void onCrash(ApplicationCrash crash);
    }

    @FunctionalInterface
    public interface CollectTextures {
        void onCollectTextures(TextureCollection collection);
    }

    @FunctionalInterface
    public interface ClientStarted {
        void onClientStarted(BubbleBlaster loader);
    }
}
