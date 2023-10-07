package com.ultreon.bubbles.common.random;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.bubble.BubbleProperties;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.random.JavaRandom;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Random properties generator for bubbles.
 *
 * @author XyperCode
 * @see Bubble
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class BubbleRandomizer extends EntityRandomizer<Bubble> {
    /**
     * Generate random properties for a bubble.
     *
     * @param bounds      the boundaries where the spawning positions can be in.
     * @param random      the RandomSource to generate the random properties.
     * @param world the world object holding the in-game state.
     * @param bubble      the bubble to randomize the properties for.
     * @return the randomly generated properties.
     */
    @Override
    public BubbleProperties randomProperties(Rectangle bounds, RandomSource random, int retry, World world, Bubble bubble) {
        var rng = new JavaRandom(random.nextLong() ^ retry);

        var type = bubble.getBubbleType();

        // Properties
        var hardness = type.getHardness().getValue();
        var speed = (float) type.getSpeed().getValue();
        var radius = (float) type.getRadius().getValue();

        int x, y;
        if (bounds.getX() == bounds.getX() + bounds.getWidth() || bounds.getY() == bounds.getY() + bounds.getHeight()) {
            x = 0;
            y = 0;
        } else {
            x = rng.nextInt((int) bounds.getX(), (int) bounds.getX() + (int) bounds.getWidth());
            y = rng.nextInt((int) bounds.getY(), (int) bounds.getY() + (int) bounds.getHeight());
        }

        var rad = (int) (radius + (type.getColors().size() * 3) + 4);
        var defense = (float) type.getDefense().getValue();
        var attack = (float) type.getAttack().getValue();
        var score = (float) type.getScore().getValue();

        return new BubbleProperties(type, radius, speed, rad, x, y, defense, attack, score);
    }
}
