package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.Registrable;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.ai.AiTask;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.commons.exceptions.InvalidValueException;
import com.ultreon.commons.lang.Pair;
import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.lang.math.IntRange;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.*;

/**
 * @see EntityType
 */
@SuppressWarnings({"unused", "SameParameterValue", "SameReturnValue"})
public abstract class BubbleType extends Registrable implements Serializable, com.ultreon.bubbles.common.text.translation.Translatable {
    public Color[] colors;
    private double priority;

    private IntRange radius;
    private DoubleRange speed;
    private float bounceAmount;
    private BubbleEffectCallback effect = (source, target) -> null;

    private float score;
    private float defense = 1f;
    private float attack = 0.2f;
    private double hardness;
    private int rarity;

    private final List<AiTask> aiTasks = new ArrayList<>();

    public BubbleType() {
    }

    public void addAiTask(int i, AiTask task) {
        this.aiTasks.add(i, task);
    }

    public Iterable<AiTask> getAiTasks() {
        return Collections.unmodifiableList(aiTasks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BubbleType that = (BubbleType) o;
        return that.id().equals(id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Effect     //
    ////////////////////
    public AppliedEffect getEffect(Bubble source, Entity target) {
        return effect.get(source, target);
    }

    protected final void setEffect(BubbleEffectCallback effect) {
        this.effect = effect;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Attributes     //
    ////////////////////////
    public int getMinRadius() {
        return radius.getMinimumInteger();
    }

    public int getMaxRadius() {
        return radius.getMaximumInteger();
    }

    public double getMinSpeed() {
        return speed.getMinimumDouble();
    }

    public double getMaxSpeed() {
        return speed.getMaximumDouble();
    }

    protected final void setMinRadius(int radius) {
        this.radius = new IntRange(radius, this.radius.getMaximumInteger());
    }

    protected final void setMaxRadius(int radius) {
        this.radius = new IntRange(this.radius.getMinimumInteger(), radius);
    }

    protected final void setMinSpeed(double speed) {
        this.speed = new DoubleRange(speed, this.radius.getMaximumDouble());
    }

    protected final void setMaxSpeed(double speed) {
        this.speed = new DoubleRange(this.speed.getMinimumInteger(), speed);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Modifiers     //
    ///////////////////////
    public boolean isDefenseRandom() {
        return false;
    }

    public boolean isAttackRandom() {
        return false;
    }

    public boolean isScoreRandom() {
        return false;
    }

    public float getDefense(Environment environment, Rng rng) {
        return getDefense();
    }

    public float getAttack(Environment environment, Rng rng) {
        return getAttack();
    }

    public float getScore(Environment environment, Rng rng) {
        return getScore();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Other     //
    ///////////////////
    public double getModifiedPriority(double localDifficulty) {
        return getPriority();
    }

    public boolean canSpawn(@NonNull Environment environment) {
        return true;
    }

    public ArrayList<Object> getFilters(Bubble bubble) {
        return new ArrayList<>();
    }

    @FunctionalInterface
    public interface BubbleEffectCallback {
        AppliedEffect get(Bubble source, Entity target);
    }

    public double getPriority() {
        return priority;
    }

    public IntRange getRadius() {
        return radius;
    }

    public DoubleRange getSpeed() {
        return speed;
    }

    public float getBounceAmount() {
        return bounceAmount;
    }

    public float getScore() {
        return score;
    }

    public float getDefense() {
        return defense;
    }

    public float getAttack() {
        return attack;
    }

    public double getHardness() {
        return hardness;
    }

    protected void setPriority(double priority) {
        this.priority = priority;
    }

    protected void setRadius(IntRange radius) {
        this.radius = radius;
    }

    protected void setSpeed(DoubleRange speed) {
        this.speed = speed;
    }

    protected void setBounceAmount(float bounceAmount) {
        this.bounceAmount = bounceAmount;
    }

    protected void setScore(float score) {
        this.score = score;
    }

    protected void setDefense(float defense) {
        this.defense = defense;
    }

    protected void setAttack(float attack) {
        this.attack = attack;
    }

    protected void setHardness(double hardness) {
        this.hardness = hardness;
    }

    protected void setRarity(int rarity) {
        this.rarity = rarity;
    }

    protected int getRarity() {
        return rarity;
    }

    public Color[] getColors() {
        return colors;
    }

    protected void setColors(Color[] colors) {
        this.colors = colors;
    }

    @Override
    public String toString() {
        return "Bubble{" +
                "colors=" + Arrays.toString(colors) +
                ", priority=" + priority +
                ", radius=" + radius +
                ", speed=" + speed +
                ", bounceAmount=" + bounceAmount +
                ", effect=" + effect +
                ", score=" + score +
                ", defense=" + defense +
                ", attack=" + attack +
                ", hardness=" + hardness +
                ", rarity=" + rarity +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long priority = null;
        private float score = 1f;
        private float defense = Float.MIN_NORMAL;
        private float attack = 0.2f;
        private IntRange radius = new IntRange(21, 80);
        private DoubleRange speed = new DoubleRange(1d, 2.5d);
        private int rarity;
        private float bounceAmount = 0f;
        private double hardness = 1d;
        private Color[] colors;
        private BubbleEffectCallback bubbleEffect = (source, target) -> null;
        private DoubleRange _speed;
        private boolean doesBounce = false;
        private int difficulty = -1;
        private List<Pair<Integer, AiTask>> aiTasks = new ArrayList<>();

        private Builder() {
        }

        public BubbleType build() {
            BubbleType bubbleType = new BubbleType() {
                @Override
                public double getModifiedPriority(double localDifficulty) {
                    if (difficulty == -1) return getPriority();
                    return getPriority() * (localDifficulty / 100.0f * difficulty);
                }
            };
            if (priority == null) {
                throw new IllegalArgumentException("Priority must be specified");
            }
            if (colors == null) {
                throw new IllegalArgumentException("Colors must be specified");
            }

            bubbleType.setPriority(priority);
            bubbleType.setRarity(rarity);
            bubbleType.setScore(score);
            bubbleType.setAttack(attack);
            bubbleType.setDefense(defense);
            bubbleType.setRadius(radius);
            bubbleType.setSpeed(speed);
            bubbleType.setHardness(hardness);
            bubbleType.setEffect(bubbleEffect);

            if (doesBounce) {
                bubbleType.setBounceAmount(bounceAmount);
            }

            bubbleType.colors = colors;

            for (Pair<Integer, AiTask> aiTask : aiTasks) {
                bubbleType.addAiTask(aiTask.getFirst(), aiTask.getSecond());
            }

            return bubbleType;
        }

        @Deprecated(since = "0.0.3470-indev5", forRemoval = true)
        public Builder name(String name) {
            return this;
        }

        // Longs
        public Builder priority(long priority) {
            this.priority = priority;
            return this;
        }

        // Ints
        public Builder score(float score) {
            this.score = score;
            return this;
        }

        public Builder rarity(int rarity) {
            this.rarity = rarity;
            return this;
        }

        // Doubles
        public Builder attack(float attack) {
            this.attack = attack;
            return this;
        }

        public Builder defense(float defense) {
            this.defense = defense;
            return this;
        }

        public Builder hardness(double hardness) {
            this.hardness = hardness;
            return this;
        }

        // Floats
        public Builder bounceAmount(float bounceAmount) {
            this.bounceAmount = bounceAmount;
            this.doesBounce = true;
            return this;
        }

        // Ranges
        public Builder radius(int _min, int _max) {
            this.radius = new IntRange(_min, _max);
            return this;
        }

        public Builder radius(IntRange range) {
            this.radius = range;
            return this;
        }

        public Builder speed(double _min, double _max) {
            this.speed = new DoubleRange(_min, _max);
            return this;
        }

        public Builder speed(DoubleRange range) {
            this.speed = range;
            return this;
        }

        // Arrays (Dynamic)
        public Builder colors(Color... _colors) {
            this.colors = _colors;
            return this;
        }

        // Callbacks
        public Builder effect(BubbleEffectCallback _bubbleEffect) {
            this.bubbleEffect = _bubbleEffect;
            return this;
        }

        public Builder difficulty(int difficulty) {
            if (difficulty < 0) {
                throw new IllegalArgumentException("Number can only be positive.");
            }
            this.difficulty = difficulty;
            return this;
        }

        public Builder addAiTask(int i, AiTask aiAttack) {
            this.aiTasks.add(new Pair<>(i, aiAttack));
            return this;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Collision     //
    ///////////////////////
    public void onCollision(Bubble source, Entity target) {
        AppliedEffect appliedEffect = getEffect(source, target);
        if (appliedEffect == null) {
            return;
        }

        if (source.isEffectApplied()) return;

        if (target instanceof Player player) {
            try {
                source.setEffectApplied(true);
                player.addEffect(appliedEffect);
            } catch (InvalidValueException valueError) {
                valueError.printStackTrace();
            }
        }
    }

    @Override
    public String getTranslationPath() {
        Identifier registryName = id();
        return registryName.location() + "/bubble/name/" + registryName.path();
    }

    public String getDescriptionTranslationPath() {
        Identifier registryName = id();
        return registryName.location() + "/bubble/description/" + registryName.path();
    }
}
