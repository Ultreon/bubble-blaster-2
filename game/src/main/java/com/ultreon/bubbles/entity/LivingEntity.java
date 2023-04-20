package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.event.v1.EntityEvents;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.events.v1.ValueEventResult;
import org.jetbrains.annotations.NotNull;
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
        return attributes.get(Attribute.MAX_HEALTH);
    }

    @Deprecated
    public void setMaxHealth(double baseMaxDamage) {
        attributes.setBase(Attribute.MAX_HEALTH, baseMaxDamage);
    }

    public double getSpeed() {
        return attributes.get(Attribute.SPEED);
    }

    @Deprecated
    public void setSpeed(double baseSpeed) {
        attributes.setBase(Attribute.SPEED, baseSpeed);
    }

    @Deprecated
    public double getBaseSpeed() {
        return attributes.getBase(Attribute.SPEED);
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

        ValueEventResult<Double> eventResult = EntityEvents.DAMAGE.factory().onDamage(this, source, value);
        if (eventResult.isCanceled()) return;

        if (attributes.getBase(Attribute.DEFENSE) == 0f) this.destroy();

        @Nullable Double resultValue = eventResult.getValue();
        if (resultValue != null) value = resultValue;

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
    public @NotNull MapType save() {
        @NotNull MapType tag = super.save();

        tag.putDouble("health", health);

        return tag;
    }

    @Override
    public void load(MapType data) {
        super.load(data);

        health = data.getFloat("health");
    }

    public String toSimpleString() {
        return id() + "@(" + Math.round(getX()) + "," + Math.round(getY()) + ")";
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
}
