package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.common.EntityPos;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.libs.commons.v0.vector.Vec2i;

/**
 * Bubble properties, used for {@link Gamemode} objects / classes, and used by {@link BubbleRandomizer} for returning the randomized bubble properties.
 *
 * @author XyperCode
 * @see BubbleRandomizer
 */
@SuppressWarnings("unused")
public class BubbleProperties extends EntityPos {
    private final float damageValue;
    private final double speed;
    private final int radius;
    private final float defense;
    private final float attack;
    private final float score;
    private final BubbleType type;
    private final Rng rng;

    /**
     * Bubble properties: Constructor.
     *
     * @param type        the bubble type.
     * @param damageValue the bubble hardness.
     * @param speed       the bubble speed.
     * @param radius      the bubble radius.
     * @param x           the bubble x coordinate.
     * @param y           the bubble y coordinate.
     * @param environment the game environment.
     */
    public BubbleProperties(BubbleType type, float damageValue, double speed, int radius, int x, int y, Environment environment, Rng rng) {
        super(x, y);

        // Type.
        this.type = type;

        // Values.
        this.damageValue = damageValue;
        this.speed = speed;
        this.radius = radius;

        // Attributes.
        this.defense = type.getDefense();
        this.attack = type.getAttack();
        this.score = type.getScore();
        this.rng = rng;
    }

    /**
     * Bubble properties: Constructor.
     *
     * @param type        the bubble type.
     * @param damageValue the bubble hardness.
     * @param speed       the bubble speed.
     * @param radius      the bubble radius.
     * @param x           the bubble x coordinate.
     * @param y           the bubble y coordinate.
     * @param environment the game-type where the randomizing would be used.
     * @param rng         the RNG for the bubble to generate unsolved values.
     */
    public BubbleProperties(BubbleType type, float damageValue, double speed, int radius, int x, int y, Environment environment, Rng rng, Rng bubbleRng) {
        super(x, y);

        // Type.
        this.type = type;

        // Values.
        this.damageValue = damageValue;
        this.speed = speed;
        this.radius = radius;

        // Attributes.
        this.defense = type.getDefense(environment, rng);
        this.attack = type.getAttack(environment, rng);
        this.score = type.getScore(environment, rng);
        this.rng = bubbleRng;
    }

    /**
     * Bubble properties: Constructor/
     *
     * @param type        the bubble type.
     * @param damageValue the bubble hardness.
     * @param speed       the bubble speed.
     * @param radius      the bubble radius.
     * @param x           the bubble x coordinate.
     * @param y           the bubble y coordinate.
     * @param defense     the bubble defense value.
     * @param attack      the bubble attack value.
     * @param score       the bubble score value.
     */
    public BubbleProperties(BubbleType type, float damageValue, double speed, int radius, int x, int y, float defense, float attack, float score, Rng rng) {
        super(x, y);

        // Type.
        this.type = type;

        // Values.
        this.damageValue = damageValue;
        this.speed = speed;
        this.radius = radius;

        // Attributes.
        this.defense = defense;
        this.attack = attack;
        this.score = score;
        this.rng = rng;
    }

    /**
     * @return the {@link BubbleType bubble type}.
     */
    public BubbleType getType() {
        return type;
    }

    /**
     * @return the bubble speed.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @return the bubble radius.
     */
    public int getRadius() {
        return radius;
    }

    /**
     * @return the bubble position as {@link Vec2i}.
     */
    public Vec2i getPos() {
        return new Vec2i(x, y);
    }

    /**
     * @return the bubble hardness.
     */
    public float getDamageValue() {
        return damageValue;
    }

    /**
     * @return the bubble defense value.
     */
    public float getDefense() {
        return defense;
    }

    /**
     * @return the bubble attack value.
     */
    public float getAttack() {
        return attack;
    }

    /**
     * @return the bubble score multiplier.
     */
    public float getScoreMultiplier() {
        return score;
    }
}
