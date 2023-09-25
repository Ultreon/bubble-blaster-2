package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.GameWindow;
import com.ultreon.libs.events.v1.Event;
import com.ultreon.libs.events.v1.EventResult;

public class WindowEvents {
    public static final Event<WindowClosing> WINDOW_CLOSING = Event.withResult();
    public static final Event<WindowClosed> WINDOW_CLOSED = Event.create();
    public static final Event<WindowGainedFocus> WINDOW_GAINED_FOCUS = Event.create();
    public static final Event<WindowLostFocus> WINDOW_LOST_FOCUS = Event.create();
    public static final Event<WindowMinimized> WINDOW_MINIMIZED = Event.create();
    public static final Event<WindowRestored> WINDOW_RESTORED = Event.create();
    public static final Event<WindowFullscreen> WINDOW_FULLSCREEN = Event.withResult();

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
