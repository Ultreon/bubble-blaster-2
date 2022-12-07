package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.Screen;

@Deprecated
public abstract class RenderScreenEvent extends RenderEvent {
    private final Screen screen;

    public RenderScreenEvent(Screen screen, Renderer renderer) {
        super(renderer);
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }

    public static class Before extends RenderScreenEvent {
        public Before(Screen screen, Renderer renderer) {
            super(screen, renderer);
        }
    }

    public static class After extends RenderScreenEvent {
        public After(Screen screen, Renderer renderer) {
            super(screen, renderer);
        }
    }
}
