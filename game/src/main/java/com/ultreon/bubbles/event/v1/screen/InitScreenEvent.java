package com.ultreon.bubbles.event.v1.screen;

import com.ultreon.bubbles.render.gui.screen.Screen;

@Deprecated
public class InitScreenEvent extends ScreenEvent {
    public InitScreenEvent(Screen screen) {
        super(screen);
    }
}
