package dev.ultreon.bubbles.event.v1;

import dev.ultreon.bubbles.GameWindow;
import dev.ultreon.libs.events.v1.Event;
import dev.ultreon.libs.events.v1.EventResult;

public class WindowEvents {
    public static final Event<WindowCreated> WINDOW_CREATED = Event.create();
    public static final Event<WindowClosing> WINDOW_CLOSING = Event.withResult();
    public static final Event<WindowGainedFocus> WINDOW_GAINED_FOCUS = Event.create();
    public static final Event<WindowLostFocus> WINDOW_LOST_FOCUS = Event.create();
    public static final Event<WindowMinimize> WINDOW_MINIMIZED = Event.create();
    public static final Event<WindowMinimize> WINDOW_MINIMIZED_RESTORE = Event.create();
    public static final Event<WindowMaximize> WINDOW_MAXIMIZED = Event.create();
    public static final Event<WindowMaximize> WINDOW_MAXIMIZED_RESTORE = Event.create();
    public static final Event<WindowFilesDropped> WINDOW_FILES_DROPPED = Event.create();
    public static final Event<WindowFullscreen> WINDOW_FULLSCREEN = Event.withResult();

    @FunctionalInterface
    public interface WindowCreated {
        void onWindowCreated(GameWindow window);
    }

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
    public interface WindowMinimize {
        void onWindowMinimize(GameWindow window);
    }

    @FunctionalInterface
    public interface WindowMaximize {
        void onWindowMaximize(GameWindow window);
    }

    @FunctionalInterface
    public interface WindowFilesDropped {
        void onWindowFilesDropped(GameWindow window, String[] files);
    }

    @FunctionalInterface
    public interface WindowFullscreen {
        EventResult onWindowFullscreen(GameWindow window, boolean fullscreen);
    }
}
