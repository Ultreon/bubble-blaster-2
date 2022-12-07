package com.ultreon.bubbles.entity.player.ability;

import com.ultreon.bubbles.common.Registrable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * The ability type. Used to instantiate an ability.
 *
 * @param <T> the type of ability.
 */
public class AbilityType<T extends Ability<T>> extends Registrable {
    private final Supplier<T> ability;

    /**
     * This creates the type.
     * @param ability a supplier to create the ability.
     */
    public AbilityType(Supplier<T> ability) {
        this.ability = ability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbilityType<?> that = (AbilityType<?>) o;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    /**
     * Create the ability instance.
     * @return the ability.
     */
    public T getAbility() {
        return ability.get();
    }

    @Override
    public String toString() {
        return "AbilityType[" + id() + "]";
    }
}
