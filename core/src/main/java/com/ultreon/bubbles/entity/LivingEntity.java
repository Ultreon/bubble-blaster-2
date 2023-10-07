package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.event.v1.EntityEvents;
import com.ultreon.bubbles.world.World;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Living Entity base class.
 * The class for all living entities, such as {@link Player the player}.
 *
 * @see Entity
 */
public abstract class LivingEntity extends Entity {
    protected double health;
    protected boolean invincible;

    // Constructor.
    public LivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    // Properties
    public double getHealth() {
        return this.health;
    }

    public void setHealth(double health) {
        this.health = Mth.clamp(health, 0.0, this.getMaxHealth());
    }

    public double getMaxHealth() {
        return this.getAttributes().get(Attribute.MAX_HEALTH);
    }

    @Deprecated
    public void setMaxHealth(double baseMaxDamage) {
        this.getAttributes().setBase(Attribute.MAX_HEALTH, baseMaxDamage);
    }

    @Deprecated
    public void setSpeed(double baseSpeed) {
        this.getAttributes().setBase(Attribute.SPEED, baseSpeed);
    }

    @Deprecated
    public double getBaseSpeed() {
        return this.getAttributes().getBase(Attribute.SPEED);
    }

    /**
     * Attack!!!
     *
     * @param value  the attack value.
     * @param source the damage source.
     */
    public void damage(double value, EntityDamageSource source) {
        if (this.invincible) return;

        var eventResult = EntityEvents.DAMAGE.factory().onDamage(this, source, value);
        if (eventResult.isCanceled()) return;

        if (this.attributes.getBase(Attribute.DEFENSE) == 0f) this.destroy();

        @Nullable Double resultValue = eventResult.getValue();
        if (resultValue != null) value = resultValue;

        this.health -= value / this.attributes.getBase(Attribute.DEFENSE);
        this.checkHealth();
    }

    public void destroy() {
        this.health = 0;
        this.checkHealth();
    }

    public void restoreDamage(float value) {
        this.health += value;
        this.health = Mth.clamp(this.health, 0f, this.attributes.getBase(Attribute.MAX_HEALTH));
    }

    protected void checkHealth() {
        if (this.health <= 0) {
            this.delete();
        }
    }

    @Override
    public @NotNull MapType save() {
        @NotNull MapType tag = super.save();

        tag.putDouble("health", this.health);

        return tag;
    }

    @Override
    public void load(MapType data) {
        super.load(data);

        this.health = data.getFloat("health");
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    @Override
    public boolean canBeAttackedBy(Entity other) {
        return !this.invincible && super.canBeAttackedBy(other);
    }
}
