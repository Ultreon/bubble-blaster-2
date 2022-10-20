package com.ultreon.bubbles.event.v1.screen;

import com.ultreon.bubbles.render.screen.Screen;

@Deprecated
public class OpenScreenEvent extends ScreenEvent {
    public OpenScreenEvent(Screen screen) {
        super(screen);
    }
}
