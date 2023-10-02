package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.world.World;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.libs.events.v1.Event;

public class TickEvents {
    public static final Event<TickGame> TICK_GAME = Event.create();
    public static final Event<TickEntity> TICK_ENTITY = Event.create();
    public static final Event<TickPlayer> TICK_PLAYER = Event.create();
    public static final Event<TickWorld> TICK_WORLD = Event.create();

    @FunctionalInterface
    public interface TickGame {
        void onTickGame(BubbleBlaster game);
    }

    @FunctionalInterface
    public interface TickEntity {
        void onTickGame(Entity entity);
    }

    @FunctionalInterface
    public interface TickPlayer {
        void onTickGame(Player player);
    }

    @FunctionalInterface
    public interface TickWorld {
        void onTickGame(World world);
    }
}
