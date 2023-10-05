package com.ultreon.bubbles.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.bubble.BubbleProperties;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.entity.ai.AiTask;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.spawning.NaturalSpawnReason;
import com.ultreon.bubbles.entity.spawning.SpawnInformation;
import com.ultreon.bubbles.entity.spawning.SpawnUsage;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.init.BubbleTypes;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.init.SoundEvents;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.world.World;
import com.ultreon.bubbles.world.WorldRenderer;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.util.EnumUtils;
import org.jetbrains.annotations.NotNull;

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
    protected float radius;
    protected int baseSize;
    protected float bounceAmount;
    protected float baseBounceAmount;

    // Bubble type.
    protected BubbleType bubbleType;

    // Entity type.
    private static final EntityType<Bubble> entityType = Entities.BUBBLE;

    private boolean effectApplied = false;
    private int destroyFrame;
    private boolean isBeingDestroyed;
    private Variant variant;

    public static Variant getRandomVariant(World world, RandomSource random) {
        return random.nextInt(0, GIANT_BUBBLE_RARITY) == 0 ? Variant.GIANT : Variant.NORMAL;
    }

    public Bubble(World world) {
        this(world, Variant.NORMAL);
    }

    public Bubble(World world, Variant variant) {
        super(entityType, world);

        this.variant = variant;
    }

    /**
     * Spawn Event Handler
     * On-spawn.
     *
     * @param information the entity's spawn data.
     */
    @Override
    public void preSpawn(SpawnInformation information) {
        World world = information.getWorld();
        RandomSource random = information.getRandom();

        super.preSpawn(information);

        if (information.getReason() instanceof NaturalSpawnReason reason) {
            this.bubbleType = BubbleSystem.random(random, world);

            // Get random properties
            BubbleRandomizer randomizer = world.getBubbleRandomizer();
            BubbleProperties properties = randomizer.randomProperties(world.game().getGameBounds(), random, reason.getRetry(), world, this);

            this.invincible = properties.getType().isInvincible();
            this.radius = properties.getRadius();
            this.bounceAmount = this.bubbleType.getBounceAmount();

            this.attributes.setBase(Attribute.MAX_HEALTH, properties.getDamageValue());
            this.attributes.setBase(Attribute.ATTACK, properties.getAttack());
            this.attributes.setBase(Attribute.DEFENSE, properties.getDefense());
            this.attributes.setBase(Attribute.SCORE, properties.getScoreMultiplier());
            this.attributes.setBase(Attribute.SPEED, properties.getSpeed() * BubbleBlasterConfig.BASE_BUBBLE_SPEED.get());

            this.health = properties.getDamageValue();

            // Set velocity
            this.velocity.y = 0;

            // Set attributes.
            this.attributes.setBase(Attribute.DEFENSE, 0.5f);

            this.rotation = 180;

            if (reason.getUsage() == SpawnUsage.BUBBLE_INIT_SPAWN) {
                this.pos.set(random.nextFloat(-this.radius, Gdx.graphics.getWidth() + this.radius), random.nextFloat(-this.radius, Gdx.graphics.getHeight() + this.radius));
            }
        }

        this.markAsAttackable(Entities.PLAYER);
        this.markAsCollidable(Entities.BULLET);
        this.markAsCollidable(Entities.PLAYER);

        this.make();
    }

    public AiTask nextAiTask() {
        if (this.currentAiTask != null && this.currentAiTask.executeTask(this)) {
            return this.currentAiTask;
        }
        for (AiTask task : this.bubbleType.getAiTasks()) {
            if (task.executeTask(this)) {
                return this.currentAiTask = task;
            }
        }
        return this.currentAiTask = null;
    }

    @Override
    public Iterable<AiTask> getAiTasks() {
        return this.bubbleType.getAiTasks();
    }

    @Override
    public void onCollision(Entity other, double deltaTime) {
        this.bubbleType.onCollision(this, other);

        if (this.bounceAmount > 0) {
            other.bounceOff(this, this.bounceAmount / 10f, -0.01f);
        }
    }

    /**
     * <p><b>Warning: </b><i>Unsafe method! Use {@link World#spawn(EntityType, SpawnInformation)} instead.</i></p>
     *
     * @see World#spawn(EntityType, SpawnInformation)
     * @see World#spawn(Entity, SpawnInformation)
     */
    @Override
    protected void make() {
        this.valid = true;
    }

    /**
     * <p><b>Warning: </b><i>Unsafe method! Use {@link #delete()}  removeBubble create GameType} instead.</i></p>
     *
     * @see #delete()
     */
    @Override
    protected void invalidate() {
        this.valid = false;
    }

    /**
     * @return the bubble-entity's bounds.
     */
    @Override
    public Rectangle getBounds() {
        Circle circle = this.getShape();
        Rectangle rectangle = new Rectangle(this.pos.x - circle.radius / 2, this.pos.y - circle.radius / 2, circle.radius, circle.radius);
        rectangle.width += 4;
        rectangle.height += 4;
        return rectangle;
    }

    @Override
    public boolean isBad() {
        return this.bubbleType.isBad();
    }

    /**
     * Tick bubble entity.
     *
     * @param world the world where the entity is from.
     */
    @Override
    public void tick(World world) {
        // Check player and current scene.
        Player player = this.world.getPlayer();

        this.prevPos.set(this.pos);

        if (this.isBeingDestroyed) {
            this.destroyFrame++;
            if (this.destroyFrame >= 10) {
                this.delete();
            }
        }

        super.tick(world);

        if (this.pos.x + this.radius < 0) {
            this.delete();
        }
    }

    @Override
    public boolean hasAi() {
        if (this.isBeingDestroyed) return false;
        return super.hasAi();
    }

    @Override
    public float getSpeed() {
        return super.getSpeed() * this.getLevelSpeedModifier() * this.getWorld().getGlobalBubbleSpeedModifier();
    }

    private float getLevelSpeedModifier() {
        if (BubbleBlasterConfig.DIFFICULTY_EFFECT_TYPE.get().isSpeed())
            return this.world.getPlayer().getLevel() / 2f + 1;

        return 1;
    }

    /**
     * Rendering method.<br>
     * <b>SHOULD NOT BE CALLED!</b>
     *
     * @param renderer renderer to render the bubble entity with.
     */
    @Override
    public void render(Renderer renderer) {
        if (this.willBeDeleted()) return;
//        renderer.image(TextureCollections.BUBBLE_TEXTURES.get().get(new TextureCollection.Index(getBubbleType().id().location(), getBubbleType().id().path() + "/" + radius)), (int) x - radius / 2, (int) y - radius / 2);
        WorldRenderer.drawBubble(renderer, this.pos.x, this.pos.y, this.radius, this.destroyFrame, this.bubbleType);
    }

    public boolean isBeingDestroyed() {
        return this.isBeingDestroyed;
    }

    /**
     * @return the bubble's shape.
     */
    @Override
    public Circle getShape() {
        float rad = this.radius / 2;
        return new Circle(this.pos.x - rad, this.pos.y - rad, rad);
    }

    @Override
    public boolean isVisible() {
        Rectangle bounds = BubbleBlaster.getInstance().getBounds();
        return this.pos.x + this.radius >= 0 && this.pos.y + this.radius >= 0 &&
                this.pos.x - this.radius <= bounds.width && this.pos.y - this.radius <= bounds.height;
    }

    ///////////////////////////////////////////////////////////////////////////
    //     Attributes     //
    ////////////////////////

    // Radius.
    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return this.radius;
    }

    public float radius() {
        return this.getShape().radius;
    }

    public int getBaseSize() {
        return this.baseSize;
    }

    // Bounce amount.
    public void setBounceAmount(float bounceAmount) {
        this.bounceAmount = bounceAmount;
    }

    public float getBounceAmount() {
        return this.bounceAmount;
    }

    public float getBaseBounceAmount() {
        return this.baseBounceAmount;
    }

    ///////////////////////////////////////////////////////////////////////////
    //     Bubble Type     //
    /////////////////////////
    public BubbleType getBubbleType() {
        return this.bubbleType;
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
        this.radius = (int) hardness + 4;
        this.checkHealth();
    }

    @Override
    public void damage(double value, EntityDamageSource source) {
        if (this.isBeingDestroyed) return;
        super.damage(value / this.attributes.getBase(Attribute.DEFENSE), source);
        if (this.invincible) return;
        this.pop();
    }

    public void pop() {
        if (this.isValid() && this.isVisible())
            SoundEvents.BUBBLE_POP.play(0.3f);

        this.delete();
        this.isBeingDestroyed = true;
        this.attributes.removeModifiers(Attribute.SCORE);
        this.attributes.setBase(Attribute.SCORE, 0);
        this.canCollideWith.clear();
        this.canAttack.clear();
    }

    @Override
    public @NotNull MapType save() {
        @NotNull MapType data = super.save();
        data.putFloat("radius", this.radius);
        data.putInt("baseRadius", this.baseSize);

        data.putFloat("bounceAmount", this.bounceAmount);
        data.putFloat("baseBounceAmount", this.baseBounceAmount);

        data.putBoolean("effectApplied", this.effectApplied);
        Identifier id = Registries.BUBBLES.getKey(this.bubbleType);
        data.putString("bubbleType", id == null ? BubbleTypes.NORMAL.getId().toString() : id.toString());

        return data;
    }

    @Override
    public void load(MapType data) {
        super.load(data);

        this.radius = data.getInt("radius");
        this.baseSize = data.getInt("baseRadius");

        this.bounceAmount = data.getFloat("bounceAmount");
        this.baseBounceAmount = data.getFloat("baseBounceAmount");
        this.variant = EnumUtils.byName(data.getString(this.variant.name(), ""), Variant.NORMAL);

        Identifier bubbleTypeKey = Identifier.parse(data.getString("bubbleType"));
        this.effectApplied = data.getBoolean("effectApplied");
        this.bubbleType = Registries.BUBBLES.getValue(bubbleTypeKey);
    }

    public void setEffectApplied(boolean effectApplied) {
        this.effectApplied = effectApplied;
    }

    public boolean isEffectApplied() {
        return this.effectApplied;
    }

    public enum Variant {
        NORMAL,
        GIANT
    }
}
