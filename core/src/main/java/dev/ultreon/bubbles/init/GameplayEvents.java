package dev.ultreon.bubbles.init;

import dev.ultreon.bubbles.common.gamestate.GameplayEvent;
import dev.ultreon.bubbles.gameplay.event.BloodMoonGameplayEvent;
import dev.ultreon.bubbles.gameplay.event.GoldenSpawnEvent;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * @see GameplayEvent
 */
public class GameplayEvents {
    // Bubbles
    public static final BloodMoonGameplayEvent BLOOD_MOON_EVENT = GameplayEvents.register("blood_moon", new BloodMoonGameplayEvent());
    public static final GoldenSpawnEvent GOLDEN_SPAWN_EVENT = GameplayEvents.register("golden_spawn", new GoldenSpawnEvent());

    private static <T extends GameplayEvent> T register(String name, T gameplayuEvent) {
        Registries.GAMEPLAY_EVENTS.register(new Identifier(name), gameplayuEvent);
        return gameplayuEvent;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
