package com.ultreon.bubbles.event.v2;

import com.ultreon.bubbles.render.screen.Screen;

public class ScreenEvents {
    public static final Event<Open> OPEN = Event.withResult();
    public static final Event<Init> INIT = Event.create();

    @FunctionalInterface
    public interface Open {
        EventResult<Void> onOpen(Screen screen);
    }

    @FunctionalInterface
    public interface Init {
        void onInit(Screen screen);
    }
}
