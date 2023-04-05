package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.game.GameObject;
import com.ultreon.bubbles.render.Renderer;

@Deprecated
public abstract class RenderGameObjectEvent extends RenderEvent {
    private final GameObject gameObject;

    @Deprecated()
    public RenderGameObjectEvent(GameObject gameObject, Renderer renderer) {
        super(renderer);
        this.gameObject = gameObject;
    }

    @Deprecated()
    public GameObject getGameObject() {
        return gameObject;
    }

    @Deprecated()
    public static class Before extends RenderGameObjectEvent {
        @Deprecated()
        public Before(GameObject gameObject, Renderer renderer) {
            super(gameObject, renderer);
        }
    }

    @Deprecated()
    public static class After extends RenderGameObjectEvent {
        @Deprecated()
        public After(GameObject gameObject, Renderer renderer) {
            super(gameObject, renderer);
        }
    }
}
