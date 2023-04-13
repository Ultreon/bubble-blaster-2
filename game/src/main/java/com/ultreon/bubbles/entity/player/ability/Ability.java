package com.ultreon.bubbles.entity.player.ability;

import com.ultreon.bubbles.ability.TeleportAbility;
import com.ultreon.bubbles.ability.triggers.AbilityKeyTrigger;
import com.ultreon.bubbles.ability.triggers.types.AbilityKeyTriggerType;
import com.ultreon.bubbles.common.interfaces.StateHolder;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.data.types.MapType;
import com.ultreon.data.types.MapType;
import org.jetbrains.annotations.NotNull;

/**
 * The ability class. Made for players to do stuff like {@linkplain TeleportAbility teleporting}.
 * @param <T> the subclass type.
 * @since 0.0.0
 * @author Qboi123
 */
public abstract class Ability<T extends Ability<T>> implements StateHolder {
    private final AbilityType<T> type;
    private int cooldown;
    private int value;

    /**
     * Ability constructor.
     * @param type the type of the ability.
     * @since 0.0.0
     * @author Qboi123
     */
    public Ability(AbilityType<T> type) {
        this.type = type;
    }

    /**
     * Get the key that triggers it.
     * @return the trigger key.
     * @since 0.0.0
     * @author Qboi123
     */
    public abstract int getTriggerKey();

    /**
     * Method for the trigger type create the ability.
     * @return the trigger type.
     * @since 0.0.0
     * @author Qboi123
     */
    public abstract AbilityTriggerType getTriggerType();

    /**
     * Method for have a key trigger for the Ability.
     * @return the key trigger type. Null for no key trigger.
     * @since 0.0.0
     * @author Qboi123
     */
    public AbilityKeyTriggerType getKeyTriggerType() {
        return null;
    }

    /**
     * Method for key trigger event.
     * @param trigger the key trigger.
     * @since 0.0.0
     * @author Qboi123
     */
    public void onKeyTrigger(AbilityKeyTrigger trigger) {

    }

    /**
     * Handle entity ticking.
     * @see Entity
     * @since 0.0.0
     * @author Qboi123
     */
    public void onEntityTick() {
        if (canRegenerate()) {
            this.value += getRegenerationSpeed();
        }
    }

    /**
     * Save the ability instance.
     *
     * @return the compound nbt tag.
     * @author Qboi123
     * @since 0.0.0
     */
    @Override
    public @NotNull MapType save() {
        MapType data = new MapType();
        data.putInt("cooldown", this.cooldown);
        data.putInt("value", this.value);

        return data;
    }

    /**
     * Load the entity from a compound tag.
     * @param tag the compound tag to load from.
     * @since 0.0.0
     * @author Qboi123
     */
    @Override
    public void load(MapType tag) {
        this.cooldown = tag.getInt("cooldown");
        this.value = tag.getInt("value");
    }

    /**
     * Handle trigger from a player.
     * @param trigger ability trigger instance.
     * @since 0.0.0
     * @author Qboi123
     */
    public abstract void trigger(AbilityTrigger trigger);

    /**
     * Handle trigger from a non-player entity.
     * @since 0.0.0
     * @author Qboi123
     */
    public abstract void triggerEntity();

    /**
     * Get whether the ability can be triggered by an entity.
     * @param entity the entity to check for.
     * @return true if it can be triggered.
     * @since 0.0.0
     * @author Qboi123
     */
    public abstract boolean canBeTriggered(@SuppressWarnings("unused") Entity entity);

    /**
     * Get whether the ability can regenerate.
     * @return true if it can.
     * @since 0.0.0
     * @author Qboi123
     */
    public abstract boolean canRegenerate();

    /**
     * Get the regeneration speed.
     * @return the speed to regenerate.
     * @see #canRegenerate()
     * @since 0.0.0
     * @author Qboi123
     */
    public int getRegenerationSpeed() {
        return 1;
    }

    /**
     * Get the type of the ability.
     * @return the ability type.
     * @since 0.0.0
     * @author Qboi123
     */
    public AbilityType<T> getType() {
        return type;
    }

    /**
     * Get the ability cooldown (in seconds).
     * @return the amount of seconds to cooldown.
     * @since 0.0.0
     * @author Qboi123
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Set the ability cooldown.
     * @param cooldown the amount of seconds to cooldown.
     * @since 0.0.0
     * @author Qboi123
     */
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    /**
     * Get the ability value. Something like mana or energy.
     * @return the ability value.
     * @since 0.0.0
     * @author Qboi123
     */
    public int getValue() {
        return value;
    }

    /**
     * Set the ability value. Something like mana or energy.
     * @param value the ability value.
     * @see #useValue(int)
     * @see #addValue(int)
     * @since 0.0.0
     * @author Qboi123
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Subtract value from the ability.
     * @param amount amount to subtract.
     * @see #setValue(int)
     * @deprecated use {@link #useValue(int)} instead.
     * @since 0.0.0
     * @author Qboi123
     */
    @Deprecated
    public void subtractValue(int amount) {
        this.value -= amount;
    }

    /**
     * Use up value. This is used when the ability is used.
     * @param amount amount to use.
     * @see #setValue(int)
     * @since 0.1.0
     * @author Qboi123
     */
    public void useValue(int amount) {
        this.value -= amount;
    }

    /**
     * Add value to the ability.
     * @param amount amount to add.
     * @see #setValue(int)
     * @deprecated use {@link #setValue(int)} instead.
     * @since 0.0.0
     * @author Qboi123
     */
    @Deprecated
    public void addValue(int amount) {
        this.value -= amount;
    }
}
