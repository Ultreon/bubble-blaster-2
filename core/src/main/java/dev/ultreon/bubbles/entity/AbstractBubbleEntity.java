package dev.ultreon.bubbles.entity;

import com.badlogic.gdx.math.Vector2;
import dev.ultreon.bubbles.entity.types.EntityType;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.ubo.types.MapType;

/**
 * ItemType Entity base class
 * For entities such as a {@link Bubble bubble}
 *
 * @see Entity
 */
public abstract class AbstractBubbleEntity extends LivingEntity {
    // Constructor
    public AbstractBubbleEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void onSpawn(Vector2 pos, World world) {
        this.health = this.getMaxHealth();
    }

    public void restoreDamage(double value) {
        if (this.health + value > this.getMaxHealth()) {
            this.health = this.getMaxHealth();
            return;
        }
        this.health += value;
    }

    public abstract float getRadius();

    @Override
    public void tick(World world) {
        super.tick(world);
    }

    @Override
    public void load(MapType data) {
        super.load(data);
    }

}
