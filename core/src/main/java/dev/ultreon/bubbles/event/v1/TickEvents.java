package dev.ultreon.bubbles.event.v1;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.LoadedGame;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.libs.events.v1.Event;

public class TickEvents {
    public static final Event<TickGame> PRE_TICK_GAME = Event.create();
    public static final Event<TickGame> POST_TICK_GAME = Event.create();
    public static final Event<TickEntity> PRE_TICK_ENTITY = Event.create();
    public static final Event<TickEntity> POST_TICK_ENTITY = Event.create();
    public static final Event<TickPlayer> PRE_TICK_PLAYER = Event.create();
    public static final Event<TickPlayer> POST_TICK_PLAYER = Event.create();
    public static final Event<TickWorld> PRE_TICK_WORLD = Event.create();
    public static final Event<TickWorld> POST_TICK_WORLD = Event.create();
    public static final Event<TickLoadedGame> PRE_TICK_LOADED_GAME = Event.create();
    public static final Event<TickLoadedGame> POST_TICK_LOADED_GAME = Event.create();

    @FunctionalInterface
    public interface TickGame {
        void onTickGame(BubbleBlaster game);
    }

    @FunctionalInterface
    public interface TickEntity {
        void onTickEntity(Entity entity);
    }

    @FunctionalInterface
    public interface TickPlayer {
        void onTickPlayer(Player player);
    }

    @FunctionalInterface
    public interface TickWorld {
        void onTickWorld(World world);
    }

    @FunctionalInterface
    public interface TickLoadedGame {
        void onTickLoadedGame(LoadedGame loadedGame);
    }
}
