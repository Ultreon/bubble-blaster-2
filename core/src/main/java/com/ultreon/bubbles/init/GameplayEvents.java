package com.ultreon.bubbles.init;

import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.gameplay.event.BloodMoonGameplayEvent;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * @see GameplayEvent
 */
@SuppressWarnings("unused")
public class GameplayEvents {
    // Bubbles
    public static final BloodMoonGameplayEvent BLOOD_MOON_EVENT = GameplayEvents.register("blood_moon", new BloodMoonGameplayEvent());

    private static <T extends GameplayEvent> T register(String name, T gameplayuEvent) {
        Registries.GAMEPLAY_EVENTS.register(new Identifier(name), gameplayuEvent);
        return gameplayuEvent;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
