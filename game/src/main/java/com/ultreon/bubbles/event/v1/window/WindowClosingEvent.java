package com.ultreon.bubbles.event.v1.window;

import com.ultreon.bubbles.game.GameWindow;
import com.ultreon.commons.lang.ICancellable;

@Deprecated
public class WindowClosingEvent extends WindowEvent implements ICancellable {
    public WindowClosingEvent(GameWindow window) {
        super(window);
    }
}
