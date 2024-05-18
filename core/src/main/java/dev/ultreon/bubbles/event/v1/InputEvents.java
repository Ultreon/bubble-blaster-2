package dev.ultreon.bubbles.event.v1;

import dev.ultreon.libs.events.v1.Event;

public class InputEvents {
    public static final Event<MousePress> MOUSE_PRESS = Event.create();
    public static final Event<MouseRelease> MOUSE_RELEASE = Event.create();
    public static final Event<MouseClick> MOUSE_CLICK = Event.create();
    public static final Event<MouseMove> MOUSE_MOVE = Event.create();
    public static final Event<MouseScroll> MOUSE_SCROLL = Event.create();

    public static final Event<KeyPress> KEY_PRESS = Event.create();
    public static final Event<KeyRelease> KEY_RELEASE = Event.create();
    public static final Event<CharType> CHAR_TYPE = Event.create();

    @FunctionalInterface
    public interface MousePress {
        void onMousePress(int x, int y, int button);
    }

    @FunctionalInterface
    public interface MouseRelease {
        void onMouseRelease(int x, int y, int button);
    }

    @FunctionalInterface
    public interface MouseClick {
        void onMouseClick(int x, int y, int button, int clicks);
    }

    @FunctionalInterface
    public interface MouseMove {
        void onMouseMove(int x, int y);
    }

    @FunctionalInterface
    public interface MouseDrag {
        void onMouseDrag(int x, int y, int button);
    }

    @FunctionalInterface
    public interface MouseEnterWindow {
        void onMouseEnterWindow(int x, int y);
    }

    @FunctionalInterface
    public interface MouseExitWindow {
        void onMouseExitWindow();
    }

    @FunctionalInterface
    public interface MouseScroll {
        void onMouseScroll(int x, int y, double rotation);
    }

    @FunctionalInterface
    public interface KeyPress {
        void onKeyPress(int keyCode, boolean holding);
    }

    @FunctionalInterface
    public interface KeyRelease {
        void onKeyRelease(int keyCode);
    }

    @FunctionalInterface
    public interface CharType {
        void onCharType(char c);
    }
}
