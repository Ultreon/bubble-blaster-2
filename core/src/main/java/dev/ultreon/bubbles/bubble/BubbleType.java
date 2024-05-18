package dev.ultreon.bubbles.bubble;

import com.badlogic.gdx.graphics.Texture;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.common.random.Rng;
import dev.ultreon.bubbles.effect.StatusEffectInstance;
import dev.ultreon.bubbles.entity.Bubble;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.LivingEntity;
import dev.ultreon.bubbles.entity.ai.AiTask;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.entity.types.EntityType;
import dev.ultreon.bubbles.random.valuesource.ConstantValueSource;
import dev.ultreon.bubbles.random.valuesource.RandomValueSource;
import dev.ultreon.bubbles.random.valuesource.ValueSource;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.bubbles.util.exceptions.InvalidValueException;
import dev.ultreon.bubbles.util.ColorUtils;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.commons.v0.tuple.Pair;
import dev.ultreon.libs.text.v1.Translatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see EntityType
 */
@SuppressWarnings({"SameParameterValue", "SameReturnValue"})
public abstract class BubbleType implements Serializable, Translatable {
    private static final ValueSource DEFAULT_SCORE = ConstantValueSource.of(1);
    private static final ValueSource DEFAULT_DEFENSE = ConstantValueSource.of(Float.MIN_NORMAL);
    private static final ValueSource DEFAULT_ATTACK = ConstantValueSource.of();
    private static final ValueSource DEFAULT_RADIUS = RandomValueSource.random(21, 80);
    private static final ValueSource DEFAULT_SPEED = RandomValueSource.random(1, 2.5);
    private static final ValueSource DEFAULT_HARDNESS = ConstantValueSource.of(1);
    private List<Color> colors;
    private double priority;

    private ValueSource radius = DEFAULT_RADIUS;
    private ValueSource speed = DEFAULT_SPEED;
    private float bounceAmount;
    private BubbleEffectCallback effect = (source, target) -> null;

    private ValueSource score = DEFAULT_SCORE;
    private ValueSource defense = DEFAULT_DEFENSE;
    private ValueSource attack = DEFAULT_ATTACK;
    private ValueSource hardness = DEFAULT_HARDNESS;
    private int rarity;
    private boolean invincible;

    private final List<AiTask> aiTasks = new ArrayList<>();
    private boolean isBad;

    public void addAiTask(int i, AiTask task) {
        this.aiTasks.add(i, task);
    }

    public Iterable<AiTask> getAiTasks() {
        return Collections.unmodifiableList(this.aiTasks);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Effect     //
    ////////////////////
    public StatusEffectInstance getEffect(Bubble source, Entity target) {
        return this.effect.get(source, target);
    }

    protected final void setEffect(BubbleEffectCallback effect) {
        this.effect = effect;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Attributes     //
    ////////////////////////
    @Deprecated(forRemoval = true)
    public int getMinRadius() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    public int getMaxRadius() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    public double getMinSpeed() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    public double getMaxSpeed() {
        return 0;
    }

    @Deprecated(forRemoval = true)
    protected final void setMinRadius(int radius) {

    }

    @Deprecated(forRemoval = true)
    protected final void setMaxRadius(int radius) {

    }

    @Deprecated(forRemoval = true)
    protected final void setMinSpeed(double speed) {

    }

    @Deprecated(forRemoval = true)
    protected final void setMaxSpeed(double speed) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Modifiers     //
    ///////////////////////
    @Deprecated(forRemoval = true)
    public boolean isDefenseRandom() {
        return false;
    }

    @Deprecated(forRemoval = true)
    public boolean isAttackRandom() {
        return false;
    }

    @Deprecated(forRemoval = true)
    public boolean isScoreRandom() {
        return false;
    }

    @Deprecated(forRemoval = true)
    public float getDefense(World world, Rng rng) {
        return 0.0f;
    }

    @Deprecated(forRemoval = true)
    public float getAttack(World world, Rng rng) {
        return 0.0f;
    }

    @Deprecated(forRemoval = true)
    public float getScore(World world, Rng rng) {
        return 0.0f;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Other     //
    ///////////////////
    public boolean isBad() {
        return this.isBad;
    }

    public double getModifiedPriority(double localDifficulty) {
        return this.getPriority();
    }

    public boolean canSpawn(@NotNull World world) {
        return true;
    }

    public ArrayList<Object> getFilters(Bubble bubble) {
        return new ArrayList<>();
    }

    public Identifier getId() {
        return Registries.BUBBLES.getKey(this);
    }

    @Nullable
    public Texture getInsideTexture() {
        return null;
    }

    @FunctionalInterface
    public interface BubbleEffectCallback {
        StatusEffectInstance get(Bubble source, Entity target);

    }
    public double getPriority() {
        return this.priority;
    }

    public ValueSource getRadius() {
        return this.radius;
    }

    public ValueSource getSpeed() {
        return this.speed;
    }

    public float getBounceAmount() {
        return this.bounceAmount;
    }

    public ValueSource getScore() {
        return this.score;
    }

    public ValueSource getDefense() {
        return this.defense;
    }

    public ValueSource getAttack() {
        return this.attack;
    }

    public ValueSource getHardness() {
        return this.hardness;
    }

    public List<Color> getColors() {
        return this.colors;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    protected final void setPriority(double priority) {
        this.priority = priority;
    }

    protected final void setRadius(ValueSource radius) {
        this.radius = radius;
    }

    protected final void setSpeed(ValueSource speed) {
        this.speed = speed;
    }

    protected final void setBounceAmount(float bounceAmount) {
        this.bounceAmount = bounceAmount;
    }

    protected final void setScore(ValueSource score) {
        this.score = score;
    }

    protected final void setDefense(ValueSource defense) {
        this.defense = defense;
    }

    protected final void setAttack(ValueSource attack) {
        this.attack = attack;
    }

    protected final void setHardness(ValueSource hardness) {
        this.hardness = hardness;
    }

    protected final void setRarity(int rarity) {
        this.rarity = rarity;
    }

    protected int getRarity() {
        return this.rarity;
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
                "colors=" + this.colors +
                ", priority=" + this.priority +
                ", radius=" + this.radius +
                ", speed=" + this.speed +
                ", bounceAmount=" + this.bounceAmount +
                ", effect=" + this.effect +
                ", score=" + this.score +
                ", defense=" + this.defense +
                ", attack=" + this.attack +
                ", hardness=" + this.hardness +
                ", rarity=" + this.rarity +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long priority = null;
        private ValueSource score = DEFAULT_SCORE;
        private ValueSource defense = DEFAULT_DEFENSE;
        private ValueSource attack = DEFAULT_ATTACK;
        private ValueSource radius = DEFAULT_RADIUS;
        private ValueSource speed = DEFAULT_SPEED;
        private ValueSource hardness = DEFAULT_HARDNESS;
        private boolean invulnerable = false;
        private int rarity;
        private float bounceAmount = 0f;
        private Color[] colors;
        private BubbleEffectCallback bubbleEffect = (source, target) -> null;
        private boolean doesBounce = false;
        private int difficulty = -1;
        private final List<Pair<Integer, AiTask>> aiTasks = new ArrayList<>();

        private Builder() {
        }

        public BubbleType build() {
            var bubbleType = new BubbleType() {
                @Override
                public double getModifiedPriority(double localDifficulty) {
                    if (Builder.this.difficulty == -1) return this.getPriority();
                    return this.getPriority() * (localDifficulty / 100.0f * Builder.this.difficulty);
                }
            };
            if (this.priority == null) {
                throw new IllegalArgumentException("Priority must be specified");
            }
            if (this.colors == null) {
                throw new IllegalArgumentException("Colors must be specified");
            }

            bubbleType.setPriority(this.priority);
            bubbleType.setRarity(this.rarity);
            bubbleType.setScore(this.score);
            bubbleType.setAttack(this.attack);
            bubbleType.setDefense(this.defense);
            bubbleType.setRadius(this.radius);
            bubbleType.setSpeed(this.speed);
            bubbleType.setHardness(this.hardness);
            bubbleType.setEffect(this.bubbleEffect);
            bubbleType.setInvincible(this.invulnerable);

            if (this.doesBounce) {
                bubbleType.setBounceAmount(this.bounceAmount);
            }

            bubbleType.setColors(this.colors);

            for (var aiTask : this.aiTasks) {
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

        public Builder score(int score) {
            return this.score(ConstantValueSource.of(score));
        }

        public Builder score(float score) {
            return this.score(ConstantValueSource.of(score));
        }

        public Builder score(double score) {
            return this.score(ConstantValueSource.of(score));
        }

        public Builder score(int min, int max) {
            return this.score(RandomValueSource.random(min, max));
        }

        public Builder score(float min, float max) {
            return this.score(RandomValueSource.random(min, max));
        }

        public Builder score(double min, double max) {
            return this.score(RandomValueSource.random(min, max));
        }

        public Builder score(ValueSource score) {
            this.score = score;
            return this;
        }

        public Builder rarity(int rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder attack(int attack) {
            return this.attack(ConstantValueSource.of(attack));
        }

        public Builder attack(float attack) {
            return this.attack(ConstantValueSource.of(attack));
        }

        public Builder attack(double attack) {
            return this.attack(ConstantValueSource.of(attack));
        }

        public Builder attack(int min, int max) {
            return this.attack(RandomValueSource.random(min, max));
        }

        public Builder attack(float min, float max) {
            return this.attack(RandomValueSource.random(min, max));
        }

        public Builder attack(double min, double max) {
            return this.attack(RandomValueSource.random(min, max));
        }

        public Builder attack(ValueSource attack) {
            this.attack = attack;
            return this;
        }

        public Builder defense(int defense) {
            return this.defense(ConstantValueSource.of(defense));
        }

        public Builder defense(float defense) {
            return this.defense(ConstantValueSource.of(defense));
        }

        public Builder defense(double defense) {
            return this.defense(ConstantValueSource.of(defense));
        }

        public Builder defense(int min, int max) {
            return this.defense(RandomValueSource.random(min, max));
        }

        public Builder defense(float min, float max) {
            return this.defense(RandomValueSource.random(min, max));
        }

        public Builder defense(double min, double max) {
            return this.defense(RandomValueSource.random(min, max));
        }

        public Builder defense(ValueSource defense) {
            this.defense = defense;
            return this;
        }

        public Builder hardness(int hardness) {
            return this.hardness(ConstantValueSource.of(hardness));
        }

        public Builder hardness(float hardness) {
            return this.hardness(ConstantValueSource.of(hardness));
        }

        public Builder hardness(double hardness) {
            return this.hardness(ConstantValueSource.of(hardness));
        }

        public Builder hardness(int min, int max) {
            return this.hardness(RandomValueSource.random(min, max));
        }

        public Builder hardness(float min, float max) {
            return this.hardness(RandomValueSource.random(min, max));
        }

        public Builder hardness(double min, double max) {
            return this.hardness(RandomValueSource.random(min, max));
        }

        public Builder hardness(ValueSource hardness) {
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
        public Builder radius(int radius) {
            return this.radius(ConstantValueSource.of(radius));
        }

        public Builder radius(float radius) {
            return this.radius(ConstantValueSource.of(radius));
        }

        public Builder radius(double radius) {
            return this.radius(ConstantValueSource.of(radius));
        }

        public Builder radius(int min, int max) {
            return this.radius(RandomValueSource.random(min, max));
        }

        public Builder radius(float min, float max) {
            return this.radius(RandomValueSource.random(min, max));
        }

        public Builder radius(double min, double max) {
            return this.radius(RandomValueSource.random(min, max));
        }

        public Builder radius(ValueSource range) {
            this.radius = range;
            return this;
        }

        public Builder speed(int speed) {
            return this.speed(ConstantValueSource.of(speed));
        }

        public Builder speed(float speed) {
            return this.speed(ConstantValueSource.of(speed));
        }

        public Builder speed(double speed) {
            return this.speed(ConstantValueSource.of(speed));
        }

        public Builder speed(int min, int max) {
            return this.speed(RandomValueSource.random(min, max));
        }

        public Builder speed(float min, float max) {
            return this.speed(RandomValueSource.random(min, max));
        }

        public Builder speed(double min, double max) {
            return this.speed(RandomValueSource.random(min, max));
        }

        public Builder speed(ValueSource range) {
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
        if (target instanceof LivingEntity && ((LivingEntity) target).isInvincible()) {
            var livingEntity = (LivingEntity) target;
            return;
        }

        var appliedEffect = this.getEffect(source, target);
        if (appliedEffect == null) {
            return;
        }

        if (source.isEffectApplied()) return;

        if (target instanceof Player) {
            var player = (Player) target;
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
        var registryName = Registries.BUBBLES.getKey(this);
        assert registryName != null;
        return registryName.location() + ".bubble." + registryName.path();
    }

    public String getDescriptionTranslationPath() {
        var registryName = Registries.BUBBLES.getKey(this);
        assert registryName != null;
        return registryName.location() + ".bubble." + registryName.path() + ".desc";
    }
}
