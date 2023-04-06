package com.ultreon.bubbles.common.random;

import com.ultreon.bubbles.bubble.BubbleProperties;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.geom.Rectangle2D;

/**
 * Bubble randomizer class.
 *
 * @author Qboi
 */
@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class BubbleRandomizer extends EntityRandomizer {
    public static final int BUBBLE_RNG_ID = 1399689663;
    private final PseudoRandom random;
    private final Rng variantRng;
    private final Rng hardnessRng;
    private final Rng speedRng;
    private final Rng radiusRng;
    private final Rng xRng;
    private final Rng yRng;
    private final Rng defenseRng;
    private final Rng attackRng;
    private final Rng scoreMultiplierRng;
    private final Rng bubbleRng;

    /**
     * Create bubble randomizer instance.
     *
     * @param environment the game-type to assign the randomizer to.
     * @param rng         the rng to randomize with.
     */
    public BubbleRandomizer(Environment environment, Rng rng) {
        this.random = new PseudoRandom(rng.getNumber(Integer.MIN_VALUE, (long) Integer.MAX_VALUE, 1));
        this.variantRng = new Rng(this.random, BUBBLE_RNG_ID, 1599095222);
        this.hardnessRng = new Rng(this.random, BUBBLE_RNG_ID, -603965851);
        this.speedRng = new Rng(this.random, BUBBLE_RNG_ID, -1068758051);
        this.radiusRng = new Rng(this.random, BUBBLE_RNG_ID, -1872573691);
        this.xRng = new Rng(this.random, BUBBLE_RNG_ID, 851733880);
        this.yRng = new Rng(this.random, BUBBLE_RNG_ID, 1130525813);
        this.defenseRng = new Rng(this.random, BUBBLE_RNG_ID, -107544301);
        this.attackRng = new Rng(this.random, BUBBLE_RNG_ID, 1793557458);
        this.scoreMultiplierRng = new Rng(this.random, BUBBLE_RNG_ID, -416980079);
        this.bubbleRng = new Rng(this.random, BUBBLE_RNG_ID, -562259225);
    }

    /**
     * Get a random bubble properties instance.
     *
     * @param bounds      game bounds.
     * @param environment game type.
     * @return a random bubble properties instance.
     */
    @Override
    public BubbleProperties getRandomProperties(Rectangle2D bounds, long spawnIndex, int retry, Environment environment) {
        BubbleType type = BubbleSystem.random(variantRng, spawnIndex, retry, environment);

        // Properties
        double minHardness = type.getHardness();
        double maxHardness = type.getHardness();
        double minSpeed = type.getMinSpeed();
        double maxSpeed = type.getMaxSpeed();
        int minRad = type.getMinRadius();
        int maxRad = type.getMaxRadius();

        // Randomizing.
        double hardness = hardnessRng.getNumber(minHardness, maxHardness, spawnIndex, retry);
        double speed = speedRng.getNumber(minSpeed, maxSpeed, spawnIndex, retry);
        int radius = radiusRng.getNumber(minRad, maxRad, spawnIndex, retry);

        if (bounds.getMinX() > bounds.getMaxX() || bounds.getMinY() > bounds.getMaxY()) {
            throw new IllegalStateException("Game bounds is invalid: negative size");
        }

        if (bounds.getMinX() == bounds.getMaxX() || bounds.getMinY() == bounds.getMaxY()) {
            throw new IllegalStateException("Game bounds is invalid: zero size");
        }

        int x = xRng.getNumber((int) bounds.getMinX(), (int) bounds.getMaxX(), spawnIndex, retry);
        int y = yRng.getNumber((int) bounds.getMinY(), (int) bounds.getMaxY(), spawnIndex, retry);

        int rad = radius + (type.getColors().size() * 3) + 4;
        float defense = type.getDefense(environment, defenseRng);
        float attack = type.getAttack(environment, attackRng);
        float score = type.getScore(environment, scoreMultiplierRng);

        return new BubbleProperties(type, radius, speed, rad, x, y, defense, attack, score, bubbleRng);
    }

    public PseudoRandom getRNG() {
        return random;
    }

    public Rng getHardnessRng() {
        return hardnessRng;
    }

    public Rng getSpeedRng() {
        return speedRng;
    }

    public Rng getRadiusRnd() {
        return radiusRng;
    }

    public Rng getXRng() {
        return xRng;
    }

    public Rng getYRng() {
        return yRng;
    }

    public Rng getDefenseRng() {
        return defenseRng;
    }

    public Rng getAttackRng() {
        return attackRng;
    }

    public Rng getScoreMultiplierRng() {
        return scoreMultiplierRng;
    }

    public Rng getVariantRng() {
        return variantRng;
    }

    /**
     * Create an RNG instance.
     * It's recommended to use a randomly chosen ID (that is set in a constant). Check the code in {@link Bubble}
     *
     * @param id the id.
     * @return the created RNG instance.
     * @see Bubble#X_RNG_ID
     */
    @Override
    public Rng createRng(int id) {
        return new Rng(random, BUBBLE_RNG_ID, id);
    }
}
