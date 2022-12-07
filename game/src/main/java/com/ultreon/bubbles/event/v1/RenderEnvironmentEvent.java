package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.render.Renderer;

@Deprecated
public abstract class RenderEnvironmentEvent extends RenderEvent {
    private Environment environment;

    public RenderEnvironmentEvent(Renderer renderer, Environment environment) {
        super(renderer);
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public static class Before extends RenderEnvironmentEvent {
        public Before(Renderer renderer, Environment environment) {
            super(renderer, environment);
        }
    }

    public static class After extends RenderEnvironmentEvent {
        public After(Renderer renderer, Environment environment) {
            super(renderer, environment);
        }
    }
}
