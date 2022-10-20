package com.ultreon.bubbles.event.v2;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.game.BubbleBlaster;

public class TickEvents {
    public static final Event<TickGame> TICK_GAME = Event.create();
    public static final Event<TickEntity> TICK_ENTITY = Event.create();
    public static final Event<TickPlayer> TICK_PLAYER = Event.create();
    public static final Event<TickEnvironment> TICK_ENVIRONMENT = Event.create();

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
    public interface TickEnvironment {
        void onTickGame(Environment environment);
    }
}
