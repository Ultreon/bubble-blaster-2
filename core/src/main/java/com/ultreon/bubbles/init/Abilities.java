package com.ultreon.bubbles.init;

import com.ultreon.bubbles.ability.TeleportAbility;
import com.ultreon.bubbles.entity.player.ability.AbilityType;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Class holding all abilities.
 *
 * @since 0.0.0
 * @author XyperCode
 */
public class Abilities {
    /**
     * Teleport ability
     *
     * @since 0.0.0
     */
    public static final AbilityType<TeleportAbility> TELEPORT_ABILITY = Abilities.register("teleport", new AbilityType<>(TeleportAbility::new));

    /**
     * Register an ability.
     *
     * @param name the identifier name.
     * @param ability the ability type.
     * @return the registry supplier.
     * @param <T> the ability type.
     * @author XyperCode
     * @since 0.0.0
     */
    private static <T extends AbilityType<?>> T register(String name, T ability) {
        Registries.ABILITIES.register(new Identifier(name), ability);
        return ability;
    }

    /**
     * @author XyperCode
     * @since 0.0.0
     */
    @ApiStatus.Internal
    public static void register() {

    }
}
