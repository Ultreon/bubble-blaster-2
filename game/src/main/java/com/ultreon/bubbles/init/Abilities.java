package com.ultreon.bubbles.init;

import com.ultreon.bubbles.ability.TeleportAbility;
import com.ultreon.bubbles.entity.player.ability.AbilityType;
import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;

import java.util.function.Supplier;

/**
 * Class holding all abilities.
 *
 * @since 0.0.0
 * @author Qboi123
 */
public class Abilities {
    private static final DelayedRegister<AbilityType<?>> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registry.ABILITIES);

    /**
     * Teleport ability
     *
     * @since 0.0.0
     */
    public static final RegistrySupplier<AbilityType<TeleportAbility>> TELEPORT_ABILITY = register("Teleport", () -> new AbilityType<>(TeleportAbility::new));

    /**
     * Register an ability.
     *
     * @param name the identifier name.
     * @param supplier the supplier to instantiate the ability type.
     * @return the registry supplier.
     * @param <T> the ability type.
     * @author Qboi123
     * @since 0.0.0
     */
    private static <T extends AbilityType<?>> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     *
     * @author Qboi123
     * @since 0.0.0
     */
    public static void register() {
        REGISTER.register();
    }
}
