package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.events.v1.Event;

import java.time.Instant;

public class PlayerEvents {
    public static final Event<GameOver> GAME_OVER = Event.create();
    public static final Event<LevelUp> LEVEL_UP = Event.create();
    public static final Event<LevelDown> LEVEL_DOWN = Event.create();
    public static final Event<TimedOut> TIMED_OUT = Event.create();
    public static final Event<Shoot> SHOOT = Event.create();
    public static final Event<Boost> BOOST = Event.create();

    @FunctionalInterface
    public interface GameOver {
        void onGameOver(World world, Player player, Instant time);
    }

    @FunctionalInterface
    public interface TimedOut {
        void onTimedOut(World world, Player player);
    }

    @FunctionalInterface
    public interface LevelUp {
        void onLevelUp(Player player, int newLevel);
    }

    @FunctionalInterface
    public interface LevelDown {
        void onLevelDown(Player player, int newLevel);
    }

    @FunctionalInterface
    public interface Shoot {
        void onLevelDown(Player player, boolean forced);
    }

    @FunctionalInterface
    public interface Boost {
        void onLevelDown(Player player, boolean forced);
    }
}
