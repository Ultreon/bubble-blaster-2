package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.render.Renderer;

@Deprecated
public abstract class RenderGameEvent extends RenderEvent {
    @Deprecated()
    public RenderGameEvent(Renderer renderer) {
        super(renderer);
    }

    @Deprecated()
    public static class Before extends RenderGameEvent {
        public Before(Renderer renderer) {
            super(renderer);
        }
    }

    @Deprecated()
    public static class After extends RenderGameEvent {
        public After(Renderer renderer) {
            super(renderer);
        }
    }
}
