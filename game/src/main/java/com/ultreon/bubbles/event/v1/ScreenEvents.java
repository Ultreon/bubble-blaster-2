package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.libs.events.v1.Event;
import com.ultreon.libs.events.v1.ValueEventResult;

public class ScreenEvents {
    public static final Event<Open> OPEN = Event.withValue();
    public static final Event<Init> INIT = Event.create();
    public static final Event<Close> CLOSE = Event.cancelable();

    @FunctionalInterface
    public interface Open {
        ValueEventResult<Screen> onOpen(Screen screen);
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
