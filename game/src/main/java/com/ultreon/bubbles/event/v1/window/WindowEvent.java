package com.ultreon.bubbles.event.v1.window;

import com.ultreon.bubbles.game.GameWindow;
import com.ultreon.bubbles.event.v1.Event;

@Deprecated
public class WindowEvent extends Event {
    private final GameWindow window;

    public WindowEvent(GameWindow window) {
        this.window = window;
    }

    public GameWindow getWindow() {
        return window;
    }
}
