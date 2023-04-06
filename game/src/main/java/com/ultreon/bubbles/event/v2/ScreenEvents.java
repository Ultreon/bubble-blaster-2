package com.ultreon.bubbles.event.v2;

import com.ultreon.bubbles.render.gui.screen.Screen;

public class ScreenEvents {
    public static final Event<Open> OPEN = Event.withResult();
    public static final Event<Init> INIT = Event.create();
    public static final Event<Close> CLOSE = Event.withCancel();

    @FunctionalInterface
    public interface Open {
        EventResult<Screen> onOpen(Screen screen);
    }

    @FunctionalInterface
    public interface Init {
        void onInit(Screen screen);
    }

    @FunctionalInterface
    public interface Close {
        boolean onClose(Screen screen, boolean force);
    }
}
