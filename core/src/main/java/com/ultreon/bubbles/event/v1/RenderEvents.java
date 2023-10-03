package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.world.World;
import com.ultreon.bubbles.world.WorldRenderer;
import com.ultreon.libs.events.v1.Event;

public class RenderEvents {
    public static final Event<RenderGameBefore> RENDER_GAME_BEFORE = Event.create();
    public static final Event<RenderGameAfter> RENDER_GAME_AFTER = Event.create();
    public static final Event<RenderEntityBefore> RENDER_ENTITY_BEFORE = Event.create();
    public static final Event<RenderEntityAfter> RENDER_ENTITY_AFTER = Event.create();
    public static final Event<RenderPlayerBefore> RENDER_PLAYER_BEFORE = Event.create();
    public static final Event<RenderPlayerAfter> RENDER_PLAYER_AFTER = Event.create();
    public static final Event<RenderWorldBefore> RENDER_WORLD_BEFORE = Event.create();
    public static final Event<RenderWorldAfter> RENDER_WORLD_AFTER = Event.create();
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
        void onRenderEntityAfter(Entity entity, Renderer renderer);
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
    public interface RenderWorldBefore {
        void onRenderWorldBefore(World world, WorldRenderer worldRenderer, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderWorldAfter {
        void onRenderWorldAfter(World world, WorldRenderer worldRenderer, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderScreenBefore {
        void onRenderScreenBefore(Screen screen, Renderer renderer);
    }

    @FunctionalInterface
    public interface RenderScreenAfter {
        void onRenderScreenAfter(Screen screen, Renderer renderer);
    }
}
