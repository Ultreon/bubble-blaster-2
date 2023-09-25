package com.ultreon.bubbles.common.random;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.common.EntityPos;
import com.ultreon.bubbles.environment.Environment;

/**
 * Abstract Entity Randomizer.
 */
public abstract class EntityRandomizer {
    public abstract EntityPos getRandomProperties(Rectangle bounds, long spawnIndex, int retry, Environment gameType);

    public abstract Rng createRng(int id);
}
