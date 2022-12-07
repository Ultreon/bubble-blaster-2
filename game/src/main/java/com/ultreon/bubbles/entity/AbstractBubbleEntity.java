package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.vector.Vec2f;
import net.querz.nbt.tag.CompoundTag;

/**
 * ItemType Entity base class
 * For entities such as a {@link Bubble bubble}
 *
 * @see Entity
 */
@SuppressWarnings("unused")
public abstract class AbstractBubbleEntity extends LivingEntity {
    // Constructor
    public AbstractBubbleEntity(EntityType<?> type, Environment environment) {
        super(type, environment);
    }

    @Override
    public void onSpawn(Vec2f pos, Environment environment) {
        this.health = getMaxHealth();
    }

    public void restoreDamage(double value) {
        if (health + value > getMaxHealth()) {
            this.health = getMaxHealth();
            return;
        }
        this.health += value;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    public String toSimpleString() {
        return id() + "@(" + Math.round(getX()) + "," + Math.round(getY()) + ")";
    }
}
