package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.gameplay.event.BloodMoonGameplayEvent;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;

import java.util.function.Supplier;

/**
 * @see GameplayEvent
 */
@SuppressWarnings("unused")
public class GameplayEvents {
    private static final DelayedRegister<GameplayEvent> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registry.GAMEPLAY_EVENTS);

    // Bubbles
    public static final RegistrySupplier<BloodMoonGameplayEvent> BLOOD_MOON_EVENT = register("blood_moon", BloodMoonGameplayEvent::new);

    private static <T extends GameplayEvent> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     */
    public static void register() {
        REGISTER.register();
    }
}
