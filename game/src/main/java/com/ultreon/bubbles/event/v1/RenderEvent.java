package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.render.Renderer;

@Deprecated
public abstract class RenderEvent extends Event {
    private final Renderer renderer;

    public RenderEvent(Renderer renderer) {
        this.renderer = renderer;
    }

    public Renderer getRenderer() {
        return renderer;
    }

}
