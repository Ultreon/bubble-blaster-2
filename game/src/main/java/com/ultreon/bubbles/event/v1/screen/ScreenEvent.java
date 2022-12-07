package com.ultreon.bubbles.event.v1.screen;

import com.ultreon.bubbles.render.screen.Screen;

@Deprecated
public abstract class ScreenEvent {
    private final Screen screen;

    public ScreenEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }
}
