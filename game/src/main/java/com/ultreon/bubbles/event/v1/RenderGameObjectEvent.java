package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.game.GameObject;
import com.ultreon.bubbles.render.Renderer;

@Deprecated
public abstract class RenderGameObjectEvent extends RenderEvent {
    private final GameObject gameObject;

    public RenderGameObjectEvent(GameObject gameObject, Renderer renderer) {
        super(renderer);
        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public static class Before extends RenderGameObjectEvent {
        public Before(GameObject gameObject, Renderer renderer) {
            super(gameObject, renderer);
        }
    }

    public static class After extends RenderGameObjectEvent {
        public After(GameObject gameObject, Renderer renderer) {
            super(gameObject, renderer);
        }
    }
}
