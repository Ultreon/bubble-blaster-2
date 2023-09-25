package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.environment.EnvironmentRenderer;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.libs.events.v1.Event;

public class RenderEvents {
    public static final Event<RenderGameBefore> RENDER_GAME_BEFORE = Event.create();
    public static final Event<RenderGameAfter> RENDER_GAME_AFTER = Event.create();
    public static final Event<RenderEntityBefore> RENDER_ENTITY_BEFORE = Event.create();
    public static final Event<RenderEntityAfter> RENDER_ENTITY_AFTER = Event.create();
    public static final Event<RenderPlayerBefore> RENDER_PLAYER_BEFORE = Event.create();
    public static final Event<RenderPlayerAfter> RENDER_PLAYER_AFTER = Event.create();
    public static final Event<RenderEnvironmentBefore> RENDER_ENVIRONMENT_BEFORE = Event.create();
    public static final Event<RenderEnvironmentAfter> RENDER_ENVIRONMENT_AFTER = Event.create();
    public static final Event<RenderScreenBefore> RENDER_SCREEN_BEFORE = Event.create();
    public static final Event<RenderScreenAfter> RENDER_SCREEN_AFTER = Event.create();

    @FunctionalInterface
    public interface RenderGameBefore {
        void onRenderGameBefore(BubbleBlaster game, Renderer renderer, float frameTime);
    }

    @FunctionalInterface
    public interface RenderGameAfter {
        void onRenderGameAfter(BubbleBlaster game, Renderer renderer, float frameTime);
    }

    @FunctionalInterface
    public interface RenderEntityBefore {
        void onRenderEntityBefore(Entity entity, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderEntityAfter {
        void onRenderAfter(Entity entity, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderPlayerBefore {
        void onRenderPlayerBefore(Player player, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderPlayerAfter {
        void onRenderPlayerAfter(Player player, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderEnvironmentBefore {
        void onRenderEnvironmentBefore(Environment environment, EnvironmentRenderer environmentRenderer, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderEnvironmentAfter {
        void onRenderEnvironmentAfter(Environment environment, EnvironmentRenderer environmentRenderer, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderScreenBefore {
        void onRenderScreenBefore(Screen screen, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderScreenAfter {
        void onRenderScreenAfter(Screen screen, Renderer renderer);
    }

    @FunctionalInterface
    public interface Filter {
        void onFilter(FilterBuilder builder);
    }
}
