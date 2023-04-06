package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.bubble.BubbleProperties;
import com.ultreon.bubbles.bubble.BubbleSpawnContext;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.entity.ai.AiTask;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.environment.EnvironmentRenderer;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.init.Bubbles;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.vector.Vec2f;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.geom.Ellipse2D;
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
    private static final EntityType<Bubble> entityType = Entities.BUBBLE.get();

    private final Random random = new Random(Math.round((double) (System.currentTimeMillis() / 86400000))); // Random day. 86400000 milliseconds == 1 day.
    private boolean effectApplied = false;

    public static EntityType<? extends Bubble> getRandomType(Environment environment, Rng rng) {
        if (rng.getNumber(0, GIANT_BUBBLE_RARITY, TYPE_RNG) == 0) {
            return Entities.GIANT_BUBBLE.get();
        }
        return Entities.BUBBLE.get();
    }

    public Bubble(Environment environment) {
        super(entityType, environment);

        // Add player as collidable.
        this.markAsCollidable(Entities.PLAYER.get());

        // Get random properties
        BubbleRandomizer randomizer = this.environment.getBubbleRandomizer();
        BubbleSpawnContext ctx = BubbleSpawnContext.get();
        BubbleProperties properties = randomizer.getRandomProperties(environment.game().getGameBounds(), ctx.spawnIndex(), ctx.retry(), environment);

        // Bubble Type
        this.bubbleType = properties.getType().canSpawn(environment) ? properties.getType() : Bubbles.NORMAL.get();

        // Dynamic values
        this.radius = properties.getRadius();
        setSpeed(properties.getSpeed());
        this.baseRadius = properties.getRadius();
        this.attributes.setBase(Attribute.MAX_HEALTH, properties.getDamageValue());
        this.attributes.setBase(Attribute.MAX_HEALTH, properties.getDamageValue());
        this.health = properties.getDamageValue();

        // Set attributes.
        this.attributes.setBase(Attribute.ATTACK, properties.getAttack());
        this.attributes.setBase(Attribute.DEFENSE, properties.getDefense());
        this.attributes.setBase(Attribute.SCORE, properties.getScoreMultiplier());
        this.attributes.setBase(Attribute.SPEED, properties.getSpeed());
        this.bases.setBase(Attribute.SPEED, properties.getSpeed());
        this.bounceAmount = bubbleType.getBounceAmount();

        // Set velocity
        this.velX = -getBaseSpeed();

        // Set attributes.
        this.attributes.setBase(Attribute.DEFENSE, 0.5f);

        this.rotation = 180;

        //
        markAsAttackable(Entities.PLAYER.get());
        markAsCollidable(Entities.BULLET.get());

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

        if (other instanceof Player player && bounceAmount > 0) {
            player.bounceOff(this, bounceAmount / 10f, -0.01f);
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
     * @see Environment#spawn(Entity, Vec2f)
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
        Rectangle rectangle = super.getBounds();
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
        boolean gameLoaded = this.environment.game().isInGame();

        if (player == null || !gameLoaded) {
            return;
        }

        this.prevX = x;
        this.prevY = y;

        super.tick(environment);

        if (this.x + this.radius < 0) {
            delete();
        }
    }

    @Override
    public double getSpeed() {
        return super.getSpeed() * (this.environment.getPlayer().getLevel() / 2d + 1) * getEnvironment().getGlobalBubbleSpeedModifier();
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
        EnvironmentRenderer.drawBubble(renderer, x - radius / 2.0, y - radius / 2.0, radius, bubbleType.getColors());
    }

    /**
     * @return the bubble's shape.
     */
    @Override
    public Ellipse2D getShape() {
        int rad = radius;
        return new Ellipse2D.Double(this.x - (float) rad / 2, this.y - (float) rad / 2, rad, rad);
    }

    @Override
    public boolean isVisible() {
        Rectangle bounds = BubbleBlaster.getInstance().getBounds();
        return x + radius >= 0 && y + radius >= 0 && x - radius <= bounds.width && y - radius <= bounds.height;
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
        if (this.health <= 0d || radius < 0) {
            delete();
        }
    }

    @Override
    public void setHealth(double hardness) {
        super.setHealth(hardness);
        radius = (int) hardness + 4;
        checkHealth();
    }

    @Override
    public void damage(double value, EntityDamageSource source) {
        super.damage(value / attributes.getBase(Attribute.DEFENSE), source);
        if (isValid() && isVisible()) {
            BubbleBlaster.getInstance().playSound(BubbleBlaster.id("sfx/bubble/pop"), 0.3);
        }
        delete();
        radius = (int) health + 4;
    }

    @Override
    public @NotNull CompoundTag save() {
        @NotNull CompoundTag document = super.save();
        document.putInt("radius", radius);
        document.putInt("baseRadius", baseRadius);

        document.putFloat("bounceAmount", bounceAmount);
        document.putFloat("baseBounceAmount", baseBounceAmount);

        document.putBoolean("effectApplied", effectApplied);
        document.putString("bubbleType", Registry.BUBBLES.getKey(bubbleType).toString());

        return document;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.radius = tag.getInt("radius");
        this.baseRadius = tag.getInt("baseRadius");

        this.bounceAmount = tag.getFloat("bounceAmount");
        this.baseBounceAmount = tag.getFloat("baseBounceAmount");

        Identifier bubbleTypeKey = Identifier.parse(tag.getString("bubbleType"));
        this.effectApplied = tag.getBoolean("effectApplied");
        this.bubbleType = Registry.BUBBLES.getValue(bubbleTypeKey);
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
