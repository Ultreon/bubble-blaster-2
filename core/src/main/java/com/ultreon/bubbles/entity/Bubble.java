package com.ultreon.bubbles.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.bubble.BubbleProperties;
import com.ultreon.bubbles.bubble.BubbleSpawnContext;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.entity.ai.AiTask;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.environment.EnvironmentRenderer;
import com.ultreon.bubbles.init.Bubbles;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Bubble Entity.
 * One create the most important parts create the game.
 *
 * @see AbstractBubbleEntity
 */
@SuppressWarnings({"SameParameterValue", "unused"})
public class Bubble extends AbstractBubbleEntity {
    // Rarities
    public static final int GIANT_BUBBLE_RARITY = 3000;

    // RNG IDs
    public static final int VARIANT_RNG = 1945857752;
    public static final int TYPE_RNG = -951578221;
    public static final int X_RNG_ID = 407004109;
    public static final int Y_RNG_ID = 1356169045;

    // General properties
    // Attributes
    protected int radius;
    protected int baseRadius;
    protected float bounceAmount;
    protected float baseBounceAmount;

    // Bubble type.
    protected BubbleType bubbleType;

    // Entity type.
    private static final EntityType<Bubble> entityType = Entities.BUBBLE;

    private final Random random = new Random(Math.round((double) (System.currentTimeMillis() / 86400000))); // Random day. 86400000 milliseconds == 1 day.
    private boolean effectApplied = false;
    private int destroyFrame;
    private boolean isBeingDestroyed;

    public static EntityType<? extends Bubble> getRandomType(Environment environment, Rng rng) {
        if (rng.getNumber(0, GIANT_BUBBLE_RARITY, TYPE_RNG) == 0) {
            return Entities.GIANT_BUBBLE;
        }
        return Entities.BUBBLE;
    }

    public Bubble(Environment environment) {
        super(entityType, environment);

        // FIXME: Use a more stable alternative that allows save loading. This is just a workaround.
        if (!BubbleSpawnContext.exists()) return;

        // Get random properties
        BubbleRandomizer randomizer = this.environment.getBubbleRandomizer();
        BubbleSpawnContext ctx = BubbleSpawnContext.get();
        BubbleProperties properties = randomizer.getRandomProperties(environment.game().getGameBounds(), ctx.spawnIndex(), ctx.retry(), environment);

        // Bubble Type
        this.bubbleType = properties.getType().canSpawn(environment) ? properties.getType() : Bubbles.NORMAL;

        // Dynamic values
        this.invincible = properties.getType().isInvincible();
        this.radius = properties.getRadius();
        this.baseRadius = properties.getRadius();
        this.attributes.setBase(Attribute.MAX_HEALTH, properties.getDamageValue());
        this.attributes.setBase(Attribute.MAX_HEALTH, properties.getDamageValue());
        this.health = properties.getDamageValue();

        // Set attributes.
        this.attributes.setBase(Attribute.ATTACK, properties.getAttack());
        this.attributes.setBase(Attribute.DEFENSE, properties.getDefense());
        this.attributes.setBase(Attribute.SCORE, properties.getScoreMultiplier());
        this.attributes.setBase(Attribute.SPEED, properties.getSpeed() * BubbleBlasterConfig.BASE_BUBBLE_SPEED.get());
        this.bounceAmount = bubbleType.getBounceAmount();

        // Set velocity
        this.velocity.y = 0;

        // Set attributes.
        this.attributes.setBase(Attribute.DEFENSE, 0.5f);

        this.rotation = 180;

        this.markAsAttackable(Entities.PLAYER);
        this.markAsCollidable(Entities.BULLET);
        this.markAsCollidable(Entities.PLAYER);

        xRng = environment.getBubbleRandomizer().getXRng();
        yRng = environment.getBubbleRandomizer().getYRng();
    }

    public AiTask nextAiTask() {
        if (currentAiTask != null && currentAiTask.executeTask(this)) {
            return currentAiTask;
        }
        for (AiTask task : bubbleType.getAiTasks()) {
            if (task.executeTask(this)) {
                return currentAiTask = task;
            }
        }
        return currentAiTask = null;
    }

    @Override
    public Iterable<AiTask> getAiTasks() {
        return bubbleType.getAiTasks();
    }

    @Override
    public void onCollision(Entity other, double deltaTime) {
        this.bubbleType.onCollision(this, other);

        if (this.bounceAmount > 0) {
            other.bounceOff(this, this.bounceAmount / 10f, -0.01f);
        }
    }

    /**
     * Spawn Event Handler
     * On-spawn.
     *
     * @param information the entity's spawn data.
     */
    @Override
    public void prepareSpawn(SpawnInformation information) {
        super.prepareSpawn(information);

        make();
    }

    /**
     * <p><b>Warning: </b><i>Unsafe method! Use {@link Environment#spawn(EntityType, SpawnInformation.SpawnReason, long, int)} instead.</i></p>
     *
     * @see Environment#spawn(EntityType, SpawnInformation.SpawnReason, long, int)
     * @see Environment#spawn(Entity, Vector2)
     */
    @Override
    protected void make() {
        valid = true;
    }

    /**
     * <p><b>Warning: </b><i>Unsafe method! Use {@link #delete()}  removeBubble create GameType} instead.</i></p>
     *
     * @see #delete()
     */
    @Override
    protected void invalidate() {
        valid = false;
    }

    /**
     * @return the bubble-entity's bounds.
     */
    @Override
    public Rectangle getBounds() {
        Circle circle = getShape();
        Rectangle rectangle = new Rectangle(this.pos.x - circle.radius / 2, this.pos.y - circle.radius / 2, circle.radius, circle.radius);
        rectangle.width += 4;
        rectangle.height += 4;
        return rectangle;
    }

    @Override
    public boolean isBad() {
        return bubbleType.isBad();
    }

    /**
     * Tick bubble entity.
     *
     * @param environment the environment where the entity is from.
     */
    @Override
    public void tick(Environment environment) {
        // Check player and current scene.
        Player player = this.environment.getPlayer();

        this.prevPos.set(pos);

        if (this.isBeingDestroyed) {
            this.destroyFrame++;
            if (this.destroyFrame >= 10) {
                delete();
            }
        }

        super.tick(environment);

        if (this.pos.x + this.radius < 0) {
            delete();
        }
    }

    @Override
    public boolean hasAi() {
        if (isBeingDestroyed) return false;
        return super.hasAi();
    }

    @Override
    public float getSpeed() {
        return (float) (super.getSpeed() * getLevelSpeedModifier() * getEnvironment().getGlobalBubbleSpeedModifier());
    }

    private double getLevelSpeedModifier() {
        if (BubbleBlasterConfig.DIFFICULTY_EFFECT_TYPE.get().isSpeed())
            return this.environment.getPlayer().getLevel() / 2d + 1;

        return 1d;
    }

    /**
     * Rendering method.<br>
     * <b>SHOULD NOT BE CALLED!</b>
     *
     * @param renderer renderer to render the bubble entity with.
     */
    @Override
    public void render(Renderer renderer) {
        if (willBeDeleted()) return;
//        renderer.image(TextureCollections.BUBBLE_TEXTURES.get().get(new TextureCollection.Index(getBubbleType().id().location(), getBubbleType().id().path() + "/" + radius)), (int) x - radius / 2, (int) y - radius / 2);
        EnvironmentRenderer.drawBubble(renderer, this.pos.x, this.pos.y, radius, destroyFrame, bubbleType.getColors());
    }

    public boolean isBeingDestroyed() {
        return isBeingDestroyed;
    }

    /**
     * @return the bubble's shape.
     */
    @Override
    public Circle getShape() {
        int rad = radius;
        return new Circle(this.pos.x - (float) rad / 2, this.pos.y - (float) rad / 2, rad);
    }

    @Override
    public boolean isVisible() {
        Rectangle bounds = BubbleBlaster.getInstance().getBounds();
        return this.pos.x + radius >= 0 && this.pos.y + radius >= 0 &&
                this.pos.x - radius <= bounds.width && this.pos.y - radius <= bounds.height;
    }

    ///////////////////////////////////////////////////////////////////////////
    //     Attributes     //
    ////////////////////////

    // Radius.
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public int getBaseRadius() {
        return baseRadius;
    }

    // Bounce amount.
    public void setBounceAmount(float bounceAmount) {
        this.bounceAmount = bounceAmount;
    }

    public float getBounceAmount() {
        return bounceAmount;
    }

    public float getBaseBounceAmount() {
        return baseBounceAmount;
    }

    ///////////////////////////////////////////////////////////////////////////
    //     Bubble Type     //
    /////////////////////////
    public BubbleType getBubbleType() {
        return bubbleType;
    }

    public void setBubbleType(BubbleType type) {
        this.bubbleType = type;
    }

    @Override
    public void checkHealth() {

    }

    @Override
    public void setHealth(double hardness) {
        super.setHealth(hardness);
        radius = (int) hardness + 4;
        checkHealth();
    }

    @Override
    public void damage(double value, EntityDamageSource source) {
        if (isBeingDestroyed) return;
        super.damage(value / attributes.getBase(Attribute.DEFENSE), source);
        if (invincible) return;
        if (isValid() && isVisible()) {
            BubbleBlaster.getInstance().playSound(BubbleBlaster.id("sfx/bubble/pop"), 0.3f);
        }
        isBeingDestroyed = true;
        attributes.removeModifiers(Attribute.SCORE);
        attributes.setBase(Attribute.SCORE, 0);
        canCollideWith.clear();
        canAttack.clear();
    }

    @Override
    public @NotNull MapType save() {
        @NotNull MapType data = super.save();
        data.putInt("radius", radius);
        data.putInt("baseRadius", baseRadius);

        data.putFloat("bounceAmount", bounceAmount);
        data.putFloat("baseBounceAmount", baseBounceAmount);

        data.putBoolean("effectApplied", effectApplied);
        Identifier id = Registries.BUBBLES.getKey(bubbleType);
        data.putString("bubbleType", id == null ? Bubbles.NORMAL.getId().toString() : id.toString());

        return data;
    }

    @Override
    public void load(MapType data) {
        super.load(data);

        this.radius = data.getInt("radius");
        this.baseRadius = data.getInt("baseRadius");

        this.bounceAmount = data.getFloat("bounceAmount");
        this.baseBounceAmount = data.getFloat("baseBounceAmount");

        Identifier bubbleTypeKey = Identifier.parse(data.getString("bubbleType"));
        this.effectApplied = data.getBoolean("effectApplied");
        this.bubbleType = Registries.BUBBLES.getValue(bubbleTypeKey);
    }

    public void setEffectApplied(boolean effectApplied) {
        this.effectApplied = effectApplied;
    }

    public boolean isEffectApplied() {
        return effectApplied;
    }

    @Override
    public double size() {
        return radius;
    }
}
