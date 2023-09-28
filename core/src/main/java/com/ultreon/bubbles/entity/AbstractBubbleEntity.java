package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.debug.Debug;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.data.types.MapType;

import java.util.UUID;

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
    public void onSpawn(Vector2 pos, Environment environment) {
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
    public void tick(Environment environment) {
        super.tick(environment);
    }

    @Override
    public void load(MapType data) {
        super.load(data);
    }

}
