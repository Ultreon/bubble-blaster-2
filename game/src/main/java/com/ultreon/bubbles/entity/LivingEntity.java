package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v2.EntityEvents;
import com.ultreon.bubbles.util.helpers.Mth;
import net.querz.nbt.tag.CompoundTag;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Living Entity base class.
 * The class for all living entities, such as {@link Player the player}.
 *
 * @see Entity
 */
@SuppressWarnings("unused")
public abstract class LivingEntity extends Entity {
    protected double health;
    protected boolean invincible;

    // Constructor.
    public LivingEntity(EntityType<?> type, Environment environment) {
        super(type, environment);
    }

    // Properties
    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = Mth.clamp(health, 0.0, getMaxHealth());
    }

    public double getMaxHealth() {
        return attributes.getBase(Attribute.MAX_HEALTH);
    }

    public void setMaxHealth(double maxDamage) {
        attributes.setBase(Attribute.MAX_HEALTH, maxDamage);
    }

    public double getSpeed() {
        return attributes.getBase(Attribute.SPEED);
    }

    public void setSpeed(double speed) {
        attributes.setBase(Attribute.SPEED, speed);
    }

    public double getBaseSpeed() {
        return bases.getBase(Attribute.SPEED);
    }

    /**
     * Attack!!!
     *
     * @param value  the attack value.
     * @param source the damage source.
     */
    @SuppressWarnings("unused")
    public void damage(double value, EntityDamageSource source) {
        if (invincible) return;

        if (attributes.getBase(Attribute.DEFENSE) == 0f) {
            this.destroy();
        }

        @Nullable Double resultValue = EntityEvents.DAMAGE.factory().onDamage(this, source, value).getValue();
        if (resultValue != null) {
            value = resultValue;
        }

        this.health -= value / attributes.getBase(Attribute.DEFENSE);
        this.checkHealth();
    }

    public void destroy() {
        this.health = 0;
        checkHealth();
    }

    public void restoreDamage(float value) {
        this.health += value;
        this.health = Mth.clamp(health, 0f, attributes.getBase(Attribute.MAX_HEALTH));
    }

    protected void checkHealth() {
        if (this.health <= 0) {
            this.delete();
        }
    }

    @Override
    public @NonNull CompoundTag save() {
        @NonNull CompoundTag tag = super.save();

        tag.putDouble("health", health);

        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        health = tag.getFloat("health");
    }

    public String toSimpleString() {
        return id() + "@(" + Math.round(getX()) + "," + Math.round(getY()) + ")";
    }
}
