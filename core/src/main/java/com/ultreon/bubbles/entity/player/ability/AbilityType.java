package com.ultreon.bubbles.entity.player.ability;

import java.util.function.Supplier;

/**
 * The ability type. Used to instantiate an ability.
 *
 * @param <T> the type of ability.
 */
public class AbilityType<T extends Ability<T>> {
    private final Supplier<T> ability;

    /**
     * This creates the type.
     * @param ability a supplier to create the ability.
     */
    public AbilityType(Supplier<T> ability) {
        this.ability = ability;
    }

    /**
     * Create the ability instance.
     * @return the ability.
     */
    public T getAbility() {
        return ability.get();
    }
}
