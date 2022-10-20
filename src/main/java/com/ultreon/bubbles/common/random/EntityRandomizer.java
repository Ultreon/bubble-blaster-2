package com.ultreon.bubbles.common.random;

import com.ultreon.bubbles.common.EntityPos;
import com.ultreon.bubbles.environment.Environment;

import java.awt.geom.Rectangle2D;

/**
 * Abstract Entity Randomizer.
 */
public abstract class EntityRandomizer {
    public abstract EntityPos getRandomProperties(Rectangle2D bounds, long spawnIndex, int retry, Environment gameType);

    public abstract Rng createRng(int id);
}
