package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.event.v1.utils.FontLoader;
import com.ultreon.bubbles.game.GameWindow;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.libs.crash.v0.ApplicationCrash;
import com.ultreon.libs.events.v1.Event;
import com.ultreon.libs.events.v1.EventResult;

import java.util.Locale;

public class WindowEvents {
    public static final Event<WindowClosing> WINDOW_CLOSING = Event.withResult();
    public static final Event<WindowClosed> WINDOW_CLOSED = Event.create();
    public static final Event<WindowGainedFocus> WINDOW_GAINED_FOCUS = Event.create();
    public static final Event<WindowLostFocus> WINDOW_LOST_FOCUS = Event.create();
    public static final Event<WindowMinimized> WINDOW_MINIMIZED = Event.create();
    public static final Event<WindowRestored> WINDOW_RESTORED = Event.create();
    public static final Event<WindowFullscreen> WINDOW_FULLSCREEN = Event.create();

    @FunctionalInterface
    public interface WindowClosing {
        EventResult onWindowClosing(GameWindow window);
    }

    @FunctionalInterface
    public interface WindowClosed {
        void onWindowClosed(GameWindow window);
    }

    @FunctionalInterface
    public interface WindowGainedFocus {
        void onWindowGainedFocus(GameWindow window);
    }

    @FunctionalInterface
    public interface WindowLostFocus {
        void onWindowLostFocus(GameWindow window);
    }

    @FunctionalInterface
    public interface WindowMinimized {
        void onWindowMinimized(GameWindow window);
    }

    @FunctionalInterface
    public interface WindowRestored {
        void onWindowRestored(GameWindow window);
    }

    @FunctionalInterface
    public interface WindowFullscreen {
        EventResult onWindowFullscreen(GameWindow window, boolean fullscreen);
    }
}
