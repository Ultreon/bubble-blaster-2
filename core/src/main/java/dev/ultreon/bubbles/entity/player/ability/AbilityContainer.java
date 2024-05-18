package dev.ultreon.bubbles.entity.player.ability;

import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.attribute.AttributeContainer;

import java.util.HashMap;

/**
 * Ability container. Similar to {@link AttributeContainer} but without modifiers, and with some extra additions.
 * This class is bound to a specific {@link Entity}.
 * @since 0.0.0
 * @author XyperCode
 */
public class AbilityContainer {
    private final HashMap<AbilityType<? extends Ability<?>>, Ability<?>> abilities = new HashMap<>();
    private final Entity entity;
    private Ability<?> currentAbility;

    /**
     * Constructor for the ability container.
     * @param entity the entity it's bound to.
     */
    public AbilityContainer(Entity entity) {
        this.entity = entity;
    }

    /**
     * Add an ability.
     * @param ability the ability to add.
     */
    public void add(Ability<? extends Ability<?>> ability) {
        this.abilities.put(ability.getType(), ability);
    }

    /**
     * Remove an ability.
     * @param abilityType the type of ability to remove.
     */
    public void remove(AbilityType<? extends Ability<?>> abilityType) {
        this.abilities.remove(abilityType);
    }

    /**
     * Set the active ability.
     * @param abilityType the type of ability to use.
     */
    public void setCurrent(AbilityType<? extends Ability<?>> abilityType) {
        this.currentAbility = this.abilities.get(abilityType);
    }

    /**
     * Get the currently active ability.
     * @return the ability.
     */
    public Ability<?> getCurrent() {
        return this.currentAbility;
    }

    /**
     * Get the entity the container is bound to.
     * @return the entity.
     */
    public Entity getEntity() {
        return this.entity;
    }

    /**
     * Handle ticking for the entity.
     * @see Ability#onEntityTick()
     */
    public void onEntityTick() {
        if (this.currentAbility != null) {
            this.currentAbility.onEntityTick();
        }
    }

    /**
     * Get an ability instance for the given type.
     * @param type the ability type.
     * @return the instance.
     */
    public Ability<?> get(AbilityType<? extends Ability<?>> type) {
        return this.abilities.get(type);
    }
}
