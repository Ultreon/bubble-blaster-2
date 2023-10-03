package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.events.v1.Event;
import com.ultreon.libs.events.v1.EventResult;

public class WorldEvents {
    public static final Event<WorldStarting> WORLD_STARTING = Event.create();
    public static final Event<WorldStarted> WORLD_STARTED = Event.create();
    public static final Event<WorldStopping> WORLD_STOPPING = Event.create();
    public static final Event<WorldStopped> WORLD_STOPPED = Event.create();
    public static final Event<WorldSaving> WORLD_SAVING = Event.withResult();
    public static final Event<WorldSaved> WORLD_SAVED = Event.withResult();
    public static final Event<GameplayEventTriggered> GAMEPLAY_EVENT_TRIGGERED = Event.withResult();
    public static final Event<GameplayEventDeactivated> GAMEPLAY_EVENT_DEACTIVATED = Event.withResult();

    @FunctionalInterface
    public interface WorldStarting {
        void onWorldStarting(World world);
    }

    @FunctionalInterface
    public interface WorldStarted {
        void onWorldStarted(World world);
    }

    @FunctionalInterface
    public interface WorldStopping {
        void onWorldStopping(World world);
    }

    @FunctionalInterface
    public interface WorldStopped {
        void onWorldStopped(World world);
    }

    @FunctionalInterface
    public interface WorldSaving {
        EventResult onWorldSaving(World world, GameSave save, Messenger messenger);
    }

    @FunctionalInterface
    public interface WorldSaved {
        void onWorldSaved(World world, GameSave save, Messenger messenger);
    }

    @FunctionalInterface
    public interface GameplayEventTriggered {
        EventResult onGameplayEventTriggered(World world, GameplayEvent gameplayEvent);
    }

    @FunctionalInterface
    public interface GameplayEventDeactivated {
        EventResult onGameplayEventDeactivated(World world, GameplayEvent gameplayEvent);
    }
}
