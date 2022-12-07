package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.bubble.BubbleProperties;
import com.ultreon.bubbles.bubble.BubbleSpawnContext;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.vector.Vec2f;

/**
 * Bubble Entity.
 * One create the most important parts create the game.
 *
 * @see AbstractBubbleEntity
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public class GiantBubble extends Bubble {
    // Entity type.
    private static final EntityType<Bubble> entityType = Entities.BUBBLE.get();

    public GiantBubble(Environment environment) {
        super(environment);
    }

    /**
     * Spawn Event Handler
     * On-spawn.
     *
     * @param pos         the position for spawn.
     * @param environment the environment to spawn in.
     */
    @Override
    public void onSpawn(Vec2f pos, Environment environment) {

        // Get random properties
        BubbleRandomizer randomizer = this.environment.getBubbleRandomizer();
        BubbleSpawnContext ctx = BubbleSpawnContext.get();
        BubbleProperties properties = randomizer.getRandomProperties(environment.game().getGameBounds(), ctx.spawnIndex(), ctx.retry(), environment);

        // Bubble Type
        this.bubbleType = properties.getType();

        // Bases.
        this.bases.setBase(Attribute.ATTACK, bubbleType.getAttack(this.environment, this.environment.getBubbleRandomizer().getAttackRng()));
        this.bases.setBase(Attribute.DEFENSE, bubbleType.getDefense(this.environment, this.environment.getBubbleRandomizer().getDefenseRng()));
        this.bases.setBase(Attribute.SCORE_MODIFIER, bubbleType.getScore(this.environment, this.environment.getBubbleRandomizer().getScoreMultiplierRng()));
        this.bases.setBase(Attribute.SPEED, properties.getSpeed() / (Math.PI));
        this.bases.setBase(Attribute.MAX_HEALTH, properties.getDamageValue() * 4 + 80);

        // Attributes
        this.attributes.setBase(Attribute.ATTACK, bubbleType.getAttack(this.environment, this.environment.getBubbleRandomizer().getAttackRng()));
        this.attributes.setBase(Attribute.DEFENSE, bubbleType.getDefense(this.environment, this.environment.getBubbleRandomizer().getDefenseRng()));
        this.attributes.setBase(Attribute.SCORE_MODIFIER, bubbleType.getScore(this.environment, this.environment.getBubbleRandomizer().getScoreMultiplierRng()));
        this.attributes.setBase(Attribute.SPEED, properties.getSpeed() / (Math.PI));
        this.attributes.setBase(Attribute.MAX_HEALTH, properties.getDamageValue() * 4 + 80);

        // Dynamic values
        this.radius = properties.getRadius() * 4 + 80;
        this.baseRadius = properties.getRadius() * 4 + 80;
        this.health = properties.getDamageValue() * 4 + 80;

        // Static values.
        this.bounceAmount = bubbleType.getBounceAmount();

        // Set velocity
        this.velX = -getBaseSpeed();

        make();
    }
}
