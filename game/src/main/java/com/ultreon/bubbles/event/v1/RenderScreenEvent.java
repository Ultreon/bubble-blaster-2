package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;

@Deprecated
public abstract class RenderScreenEvent extends RenderEvent {
    private final Screen screen;

    @Deprecated()
    public RenderScreenEvent(Screen screen, Renderer renderer) {
        super(renderer);
        this.screen = screen;
    }

    @Deprecated()
    public Screen getScreen() {
        return screen;
    }

    @Deprecated()
    public static class Before extends RenderScreenEvent {
        @Deprecated()
        public Before(Screen screen, Renderer renderer) {
            super(screen, renderer);
        }
    }

    @Deprecated()
    public static class After extends RenderScreenEvent {
        @Deprecated()
        public After(Screen screen, Renderer renderer) {
            super(screen, renderer);
        }
    }
}
