package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.render.Renderer;

@Deprecated
public abstract class RenderEnvironmentEvent extends RenderEvent {
    @Deprecated()
    private Environment environment;

    @Deprecated()
    public RenderEnvironmentEvent(Renderer renderer, Environment environment) {
        super(renderer);
        this.environment = environment;
    }

    @Deprecated()
    public Environment getEnvironment() {
        return environment;
    }

    @Deprecated()
    public static class Before extends RenderEnvironmentEvent {
        @Deprecated()
        public Before(Renderer renderer, Environment environment) {
            super(renderer, environment);
        }
    }

    @Deprecated()
    public static class After extends RenderEnvironmentEvent {
        @Deprecated()
        public After(Renderer renderer, Environment environment) {
            super(renderer, environment);
        }
    }
}
