package com.ultreon.bubbles.bubble;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.LivingEntity;
import com.ultreon.bubbles.entity.ai.AiTask;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.commons.exceptions.InvalidValueException;
import com.ultreon.commons.lang.Pair;
import com.ultreon.commons.util.ColorUtils;
import com.ultreon.libs.text.v0.Translatable;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see EntityType
 */
@SuppressWarnings({"unused", "SameParameterValue", "SameReturnValue"})
public abstract class BubbleType implements Serializable, Translatable {
    private List<Color> colors;
    private double priority;

    private Range<Integer> radius;
    private Range<Double> speed;
    private float bounceAmount;
    private BubbleEffectCallback effect = (source, target) -> null;

    private float score;
    private float defense = 1f;
    private float attack = 0.2f;
    private double hardness;
    private int rarity;
    private boolean invincible;

    private final List<AiTask> aiTasks = new ArrayList<>();

    public BubbleType() {
    }

    public void addAiTask(int i, AiTask task) {
        this.aiTasks.add(i, task);
    }

    public Iterable<AiTask> getAiTasks() {
        return Collections.unmodifiableList(aiTasks);
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
        return radius.getMaximum();
    }

    public int getMaxRadius() {
        return radius.getMaximum();
    }

    public double getMinSpeed() {
        return speed.getMaximum();
    }

    public double getMaxSpeed() {
        return speed.getMaximum();
    }

    protected final void setMinRadius(int radius) {
        this.radius = Range.between(radius, this.radius.getMaximum());
    }

    protected final void setMaxRadius(int radius) {
        this.radius = Range.between(this.radius.getMinimum(), radius);
    }

    protected final void setMinSpeed(double speed) {
        this.speed = Range.between(speed, this.speed.getMaximum());
    }

    protected final void setMaxSpeed(double speed) {
        this.speed = Range.between(this.speed.getMinimum(), speed);
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
    public boolean isBad() {
        return getAttack() > 0;
    }

    public double getModifiedPriority(double localDifficulty) {
        return getPriority();
    }

    public boolean canSpawn(@NotNull Environment environment) {
        return true;
    }

    public ArrayList<Object> getFilters(Bubble bubble) {
        return new ArrayList<>();
    }

    public Identifier getId() {
        return Registries.BUBBLES.getKey(this);
    }

    @FunctionalInterface
    public interface BubbleEffectCallback {
        AppliedEffect get(Bubble source, Entity target);

    }
    public double getPriority() {
        return priority;
    }

    public Range<Integer> getRadius() {
        return radius;
    }

    public Range<Double> getSpeed() {
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

    public List<Color> getColors() {
        return colors;
    }

    public boolean isInvincible() {
        return invincible;
    }

    protected final void setPriority(double priority) {
        this.priority = priority;
    }

    protected final void setRadius(Range<Integer> radius) {
        this.radius = radius;
    }

    protected final void setSpeed(Range<Double> speed) {
        this.speed = speed;
    }

    protected final void setBounceAmount(float bounceAmount) {
        this.bounceAmount = bounceAmount;
    }

    protected final void setScore(float score) {
        this.score = score;
    }

    protected final void setDefense(float defense) {
        this.defense = defense;
    }

    protected final void setAttack(float attack) {
        this.attack = attack;
    }

    protected final void setHardness(double hardness) {
        this.hardness = hardness;
    }

    protected final void setRarity(int rarity) {
        this.rarity = rarity;
    }

    protected int getRarity() {
        return rarity;
    }

    protected final void setColors(Color... colors) {
        this.colors = List.of(colors);
    }

    protected final void setColors(String hexList) {
        this.colors = List.of(ColorUtils.parseHexList(hexList));
    }

    protected void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    @Override
    public String toString() {
        return "Bubble{" +
                "colors=" + colors +
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
        private float attack = 0f;
        private boolean invulnerable = false;
        private Range<Integer> radius = Range.between(21, 80);
        private Range<Double> speed = Range.between(1d, 2.5d);
        private int rarity;
        private float bounceAmount = 0f;
        private double hardness = 1d;
        private Color[] colors;
        private BubbleEffectCallback bubbleEffect = (source, target) -> null;
        private boolean doesBounce = false;
        private int difficulty = -1;
        private final List<Pair<Integer, AiTask>> aiTasks = new ArrayList<>();

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
            bubbleType.setInvincible(invulnerable);

            if (doesBounce) {
                bubbleType.setBounceAmount(bounceAmount);
            }

            bubbleType.colors = List.of(colors);

            for (Pair<Integer, AiTask> aiTask : aiTasks) {
                bubbleType.addAiTask(aiTask.getFirst(), aiTask.getSecond());
            }

            return bubbleType;
        }

        public Builder invulnerable() {
            this.invulnerable = true;
            return this;
        }

        public Builder priority(long priority) {
            this.priority = priority;
            return this;
        }

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
            this.radius = Range.between(_min, _max);
            return this;
        }

        public Builder radius(Range<Integer> range) {
            this.radius = range;
            return this;
        }

        public Builder speed(double _min, double _max) {
            this.speed = Range.between(_min, _max);
            return this;
        }

        public Builder speed(Range<Double> range) {
            this.speed = range;
            return this;
        }

        // Arrays (Dynamic)
        public Builder colors(Color... _colors) {
            this.colors = _colors;
            return this;
        }

        public Builder colors(String hexList) {
            this.colors = ColorUtils.parseHexList(hexList);
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
        if (target instanceof LivingEntity livingEntity && livingEntity.isInvincible()) return;

        AppliedEffect appliedEffect = getEffect(source, target);
        if (appliedEffect == null) {
            return;
        }

        if (source.isEffectApplied()) return;

        if (target instanceof Player player) {
            try {
                source.setEffectApplied(true);
                player.addEffect(appliedEffect);
            } catch (InvalidValueException exception) {
                BubbleBlaster.getLogger().error("Failed to apply effect:", exception);
            }
        }
    }

    @Override
    public String getTranslationPath() {
        Identifier registryName = Registries.BUBBLES.getKey(this);
        return registryName.location() + "/bubble/name/" + registryName.path();
    }

    public String getDescriptionTranslationPath() {
        Identifier registryName = Registries.BUBBLES.getKey(this);
        return registryName.location() + "/bubble/description/" + registryName.path();
    }
}
