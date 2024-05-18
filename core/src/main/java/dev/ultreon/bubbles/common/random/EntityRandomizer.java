package dev.ultreon.bubbles.common.random;

import com.badlogic.gdx.math.Rectangle;
import dev.ultreon.bubbles.common.EntityProperties;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.random.RandomSource;
import dev.ultreon.bubbles.world.World;

/**
 * Entity properties generator.
 */
public abstract class EntityRandomizer<T extends Entity> {
    /**
     * Generate random properties for an entity.
     *
     * @param bounds      the boundaries where the spawning positions can be in.
     * @param random      the RandomSource to generate the random properties.
     * @param world the world object holding the in-game state.
     * @param entity      the entity to randomize the properties for.
     * @return the randomly generated properties.
     */
    public abstract EntityProperties randomProperties(Rectangle bounds, RandomSource random, int retry, World world, T entity);
}
