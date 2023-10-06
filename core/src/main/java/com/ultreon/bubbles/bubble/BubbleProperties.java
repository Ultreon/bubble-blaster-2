package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.common.EntityProperties;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.vector.Vec2i;

/**
 * Bubble properties, used for {@link Gamemode} objects / classes, and used by {@link BubbleRandomizer} for returning the randomized bubble properties.
 *
 * @author XyperCode
 * @see BubbleRandomizer
 */
public class BubbleProperties extends EntityProperties {
    private final float damageValue;
    private final float speed;
    private final int radius;
    private final float defense;
    private final float attack;
    private final float score;
    private final BubbleType type;

    /**
     * Bubble properties: Constructor.
     *
     * @param type        the bubble type.
     * @param damageValue the bubble hardness.
     * @param speed       the bubble speed.
     * @param radius      the bubble radius.
     * @param x           the bubble x coordinate.
     * @param y           the bubble y coordinate.
     * @param defense     the bubble defense.
     * @param attack      the bubble attack.
     * @param score       the bubble score.
     */
    public BubbleProperties(BubbleType type, float damageValue, float speed, int radius, int x, int y, float defense, float attack, float score) {
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
     * @param world the game-type where the randomizing would be used.
     * @param entity      the entity holding the properties.
     */
    public BubbleProperties(BubbleType type, float damageValue, float speed, int radius, int x, int y, World world, Entity entity) {
        super(x, y);

        // Type.
        this.type = type;

        // Values.
        this.damageValue = damageValue;
        this.speed = speed;
        this.radius = radius;

        // Attributes.
        this.defense = (float) type.getDefense().getValue();
        this.attack = (float) type.getAttack().getValue();
        this.score = (float) type.getScore().getValue();
    }

    /**
     * @return the {@link BubbleType bubble type}.
     */
    public BubbleType getType() {
        return this.type;
    }

    /**
     * @return the bubble speed.
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * @return the bubble radius.
     */
    public int getRadius() {
        return this.radius;
    }

    /**
     * @return the bubble position as {@link Vec2i}.
     */
    public Vec2i getPos() {
        return new Vec2i(this.x, this.y);
    }

    /**
     * @return the bubble hardness.
     */
    public float getDamageValue() {
        return this.damageValue;
    }

    /**
     * @return the bubble defense value.
     */
    public float getDefense() {
        return this.defense;
    }

    /**
     * @return the bubble attack value.
     */
    public float getAttack() {
        return this.attack;
    }

    /**
     * @return the bubble score multiplier.
     */
    public float getScoreMultiplier() {
        return this.score;
    }
}
