package com.ultreon.bubbles.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.ultreon.bubbles.common.interfaces.StateHolder;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.ai.AiTask;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.player.ability.AbilityType;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GameObject;
import com.ultreon.bubbles.init.Bubbles;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.vector.Vec2f;
import com.ultreon.commons.util.CollisionUtil;
import com.ultreon.data.types.ListType;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.translations.v0.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

/**
 * Entity base class.
 * The base for all entities, such as the player or a bubble.
 *
 * @author XyperCode
 * @see LivingEntity
 * @see AbstractBubbleEntity
 * @since 0.0.0
 */
@SuppressWarnings("unused")
public abstract class Entity extends GameObject implements StateHolder {
    // ID's
    protected long entityId;
    private UUID uniqueId;

    // Types
    protected EntityType<?> type;

    // Misc
    protected Shape2D shape;
    protected final HashSet<EntityType<?>> canCollideWith = new HashSet<>();
    protected final HashSet<EntityType<?>> canAttack = new HashSet<>();
    protected final HashSet<EntityType<?>> invulnerableTo = new HashSet<>();
    protected final Set<StatusEffectInstance> activeEffects = new CopyOnWriteArraySet<>();

    // Attributes
    protected float scale = 1;
    protected AttributeContainer attributes = new AttributeContainer();
    protected AttributeContainer bases = new AttributeContainer();

    // Flags
    protected boolean mobile = true;

    protected float x, y;
    protected float prevX, prevY;
    protected float velocityX;
    protected float velocityY;

    protected float accelerateX = 0.0f;
    protected float accelerateY = 0.0f;
    protected boolean valid;
    private boolean spawned;
    protected final Environment environment;

    // Tag
    private MapType tag = new MapType();

    // Fields.
    protected float rotation = 0;

    // Abilities.
    private final HashMap<AbilityType<?>, MapType> abilities = new HashMap<>();
    private boolean willBeDeleted;
    protected Rng xRng;
    protected Rng yRng;
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private Entity currentTarget;
    @Nullable
    protected AiTask currentAiTask;
    private final List<AiTask> aiTasks = new ArrayList<>();

    /**
     * The entity constructor.
     * @param type the type of entity to use.
     * @param environment the environment where it would spawn in.
     */
    public Entity(EntityType<?> type, Environment environment) {
        this.environment = environment;
        this.entityId = environment.getEntityId(this);
        this.type = type;
        this.uniqueId = UUID.randomUUID();
    }

    /**
     * Insert an AI-task at a given index.
     * @param i the index.
     * @param task the task.
     */
    protected final void addAiTask(int i, AiTask task) {
        this.aiTasks.add(i, task);
    }

    /**
     * Get all available AI-tasks.
     * @return the AI-tasks.
     */
    public Iterable<AiTask> getAiTasks() {
        return Collections.unmodifiableList(aiTasks);
    }

    /**
     * Get entity uuid.
     *
     * @return the entity's uuid.
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Get entity id.
     *
     * @return the entity's id.
     */
    public long getEntityId() {
        return entityId;
    }

    /**
     * Prepare spawning of the entity.
     * @param information the spawn information
     */
    public void prepareSpawn(SpawnInformation information) {
        @Nullable Vec2f pos = information.getPos();
        if (pos != null) {
            this.x = pos.x;
            this.y = pos.y;
        }
    }

    /**
     * On spawn.
     *
     * @param pos         the position to spawn at.
     * @param environment te environment to spawn in.
     */
    public void onSpawn(Vec2f pos, Environment environment) {
        spawned = true;
    }

    /**
     * Handle when the entity was teleported.
     * To cancel teleport see {@link #onTeleporting(Point2D, Point2D)}
     *
     * @param from teleport origin.
     * @param to teleport destination.
     */
    public void onTeleported(Point2D from, Point2D to) {

    }

    /**
     * Handle teleporting before it happens. Can be cancelled by returning true.
     *
     * @param from teleport origin.
     * @param to teleport destination.
     * @return true to cancel, false to pass.
     */
    public boolean onTeleporting(Point2D from, Point2D to) {
        return false;
    }

    /**
     * Handle collision between this entity and another.
     * Note that this is called periodically, but whenever it can.
     * @param other the other entity.
     * @param deltaTime the delta time from the check before this one. (This is for managing correct attack damage for example)
     */
    public abstract void onCollision(Entity other, double deltaTime);

    /**
     * Handles deletion of the entity.
     * Override for cleaning up.
     *
     * @see #delete()
     */
    protected void onDelete() {

    }

    /**
     * @return True if the events are bound, false otherwise.
     */
    protected boolean isValid() {
        return valid;
    }

    /**
     * Apply force using velocity.
     *
     * @param velocityX the amount velocity for bounce.
     * @param velocityY the amount velocity for bounce.
     * @param delta     the delta change.
     */
    public void applyForce(float velocityX, float velocityY, float delta) {
        setAcceleration(velocityX, velocityY);
    }

    private void setAcceleration(float x, float y) {
        accelerateX = x;
        accelerateY = y;
    }

    /**
     * Trigger a Reflection
     * Triggers a reflection, there are some problems with the velocity.
     * That's why it's currently in beta.
     *
     * @param velocity the amount velocity for bounce.
     * @param delta    the delta change.
     */
    public void applyForce(Vec2f velocity, float delta) {
        this.applyForce(velocity.getX(), velocity.getY(), delta);
    }

    /**
     * Apply a force towards a direction.
     *
     * @param direction the direction (in degrees).
     * @param velocity the amount velocity for bounce.
     * @param delta    the delta change.
     */
    public void applyForceDir(float direction, float velocity, float delta) {
        float x = MathUtils.cos(direction) * velocity;
        float y = MathUtils.sin(direction) * velocity;
        this.applyForce(x, y, delta);
    }

    /**
     * Bounce off another entity, with given amount of velocity.
     *
     * @param source   the source entity that triggers the bounce.
     * @param velocity the amount velocity for bounce.
     * @param delta    the delta change.
     */
    public void bounceOff(Entity source, float velocity, float delta) {
        this.applyForceDir((float) Math.toDegrees(Math.atan2(source.getY() - y, source.getX() - x)), velocity, delta);
    }

    /**
     * @return the x acceleration.
     */
    public float getAccelerateX() {
        return accelerateX;
    }

    /**
     * @return the y acceleration.
     */
    public float getAccelerateY() {
        return accelerateY;
    }

    /**
     * @param accelerateX the x acceleration to set.
     */
    public void setAccelerateX(float accelerateX) {
        this.accelerateX = accelerateX;
    }

    /**
     * @param accelerateY the y acceleration to set.
     */
    public void setAccelerateY(float accelerateY) {
        this.accelerateY = accelerateY;
    }

    /**
     * Tick event.
     *
     * @param environment the environment where the entity is from.
     */
    public void tick(Environment environment) {
        for (StatusEffectInstance appliedEffect : this.activeEffects) {
            appliedEffect.tick(this);
        }

        this.activeEffects.removeIf((effectInstance -> effectInstance.getRemainingTime() < 0d));

        this.accelerateX = this.getAccelerateX() / ((0.05f / (1 * (float) TPS / 20)) + 1);
        this.accelerateY = this.getAccelerateY() / ((0.05f / (1 * (float) TPS / 20)) + 1);

        // Calculate Velocity X and Y.
        double angelRadians = Math.toRadians(this.getRotation());
        this.velocityX = (float) (Math.cos(angelRadians) * this.getSpeed());
        this.velocityY = (float) (Math.sin(angelRadians) * this.getSpeed());

        this.prevX = this.getX();
        this.prevY = this.getY();
        this.x += this.isMobile() ? this.getAccelerateX() + this.getVelocityX() / TPS : 0;
        this.y += this.isMobile() ? this.getAccelerateY() + this.getVelocityY() / TPS : 0;

        if (hasAi()) {
            nextAiTask();

            if (currentTarget != null) {
                if (!currentTarget.isValid()) {
                    currentTarget = null;
                }
            }

            if (currentTarget != null) {
                double angleTo = this.getAngleTo(currentTarget);
                setRotation((float) angleTo);
            }
        }
    }

    /**
     * Execute and get the new AI-task.
     * @return the new AI-task.
     */
    @SuppressWarnings("UnusedReturnValue")
    public AiTask nextAiTask() {
        for (AiTask task : aiTasks) {
            if (task.executeTask(this)) {
                return currentAiTask = task;
            }
        }
        return currentAiTask = null;
    }

    /**
     * Get whether the entity has AI.
     * @return true if it has.
     */
    public boolean hasAi() {
        return true;
    }

    /**
     * Marks the entity as deleted.
     *
     * @see #willBeDeleted()
     * @see #onDelete()
     */
    public final void delete() {
        if (isValid()) {
            invalidate();
            valid = false;
        }

        onDelete();

        willBeDeleted = true;
    }

    /**
     * Get the shape create the entity.
     *
     * @return the requested shape.
     */
    public abstract Shape2D getShape();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return entityId == entity.entityId;
    }

    protected abstract void make();

    protected abstract void invalidate();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Get Bounds     //
    ////////////////////////
    public abstract Rectangle getBounds();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Getters     //
    /////////////////////
    public final Identifier id() {
        return Registries.ENTITIES.getKey(type);
    }

    public EntityType<?> getType() {
        return type;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Teleport and Position     //
    ///////////////////////////////////
    public final void teleport(double x, double y) {
        this.teleport(new Point2D.Double(x, y));
    }

    public final void teleport(Point2D pos) {
        if (onTeleporting(new Point2D.Double(x, y), new Point2D.Double(pos.getX(), pos.getY()))) return;
        this.x = (float) pos.getX();
        this.y = (float) pos.getY();
        onTeleported(new Point2D.Double(x, y), new Point2D.Double(pos.getX(), pos.getY()));
    }

    public Vec2f getPos() {
        return new Vec2f(getX(), getY());
    }

    //****************//
    //     Motion     //
    //****************//
    public void move(double deltaX, double deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    public Point2D.Double getVelocity() {
        return new Point2D.Double(velocityX, velocityY);
    }

    public void setVelocity(float velX, float velY) {
        this.velocityX = velX;
        this.velocityY = velY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Convert this entity to a simple string used for logging.
     * @return the simple string.
     * @see #toAdvancedString()
     */
    public String toSimpleString() {
        return id() + ":(" + Math.round(getX()) + "," + Math.round(getY()) + ")";
    }

    /**
     * Convert this entity to an advanced string used for debugging / development.
     * @return the advanced string.
     * @see #toSimpleString()
     */
    public final String toAdvancedString() {
        @NotNull MapType nbt = save();
        String data = nbt.toString();

        return id() + ":" + data;
    }

    /**
     * Get the active status effects.
     *
     * @return the active effects.
     */
    public Set<StatusEffectInstance> getActiveEffects() {
        return activeEffects;
    }

    /**
     * Add an effect to the entity.
     * @param appliedEffect the applied effect to add.
     */
    public void addEffect(StatusEffectInstance appliedEffect) {
        for (StatusEffectInstance appliedEffect1 : activeEffects) {
            if (appliedEffect1.getType() == appliedEffect.getType()) {
                if (appliedEffect1.getRemainingTime() < appliedEffect.getRemainingTime()) {
                    appliedEffect1.setRemainingTime(appliedEffect.getRemainingTime());
                }
                return;
            }
        }
        activeEffects.add(appliedEffect);
        appliedEffect.start(this);
    }

    /**
     * Remove a status effect from the entity.
     * @param appliedEffect the applied status effect.
     */
    public void removeEffect(StatusEffectInstance appliedEffect) {
        activeEffects.remove(appliedEffect);
        if (appliedEffect.isActive()) {
            appliedEffect.stop(this);
        }
    }

    /**
     * Load an entity without having the entity type.
     * This will load the type of entity too.
     *
     * @param environment the environment to load it in.
     * @param tags the nbt data.
     * @return the loaded entity.
     */
    @Nullable
    public static Entity loadFully(Environment environment, MapType tags) {
        Identifier type = Identifier.tryParse(tags.getString("type"));
        if (type == null) return null;
        EntityType<?> entityType = Registries.ENTITIES.getValue(type);
        return entityType == null ? null : entityType.create(environment, tags);
    }

    /**
     * Load the entity from a compound nbt tag.
     * @param data the compound to load from.
     */
    @Override
    public void load(MapType data) {
        this.tag = data.getMap("Tag");
        this.attributes.load(data.getList("Attributes"));
        this.attributes.loadModifiers(data.getList("AttributeModifiers"));

        MapType positionTag = data.getMap("Position");
        this.x = positionTag.getFloat("x");
        this.y = positionTag.getFloat("y");

        MapType previousTag = data.getMap("PrevPosition");
        this.prevX = previousTag.getFloat("x");
        this.prevY = previousTag.getFloat("y");

        MapType velocityTag = data.getMap("Velocity");
        this.velocityX = velocityTag.getFloat("x");
        this.velocityY = velocityTag.getFloat("y");

        ListType<MapType> activeEffectsData = data.getList("ActiveEffects");
        clearEffects();
        for (var activeEffectData : activeEffectsData) {
            this.activeEffects.add(StatusEffectInstance.load(activeEffectData));
        }

        this.entityId = data.getLong("id");
        this.uniqueId = data.getUUID("uuid");
        this.scale = data.getFloat("scale");
        this.rotation = data.getFloat("rotation");
    }

    /**
     * Save the entity to a compound nbt tag.
     *
     * @return the saved data in the form of a compound.
     */
    @Override
    public @NotNull MapType save() {
        // Save components.
        var data = new MapType();
        data.put("Tag", this.tag);
        data.put("Attributes", this.attributes.save());
        data.put("AttributesModifiers", this.attributes.saveModifiers());

        // Save position.
        var positionTag = new MapType();
        positionTag.putFloat("x", x);
        positionTag.putFloat("y", y);
        data.put("Position", positionTag);

        var previousTag = new MapType();
        previousTag.putDouble("x", prevX);
        previousTag.putDouble("y", prevY);
        data.put("PrevPosition", previousTag);

        // Velocity.
        var velocityTag = new MapType();
        velocityTag.putDouble("x", velocityX);
        velocityTag.putDouble("y", velocityY);
        data.put("Velocity", velocityTag);

        var activeEffectsTag = new ListType<MapType>();
        for (var instance : activeEffects)
            activeEffectsTag.add(instance.save());
        data.put("ActiveEffects", activeEffectsTag);

        // Other properties.
        data.putLong("id", this.entityId);
        data.putUUID("uuid", this.uniqueId);
        data.putDouble("scale", this.scale);
        data.putFloat("rotation", this.rotation);

        var key = Registries.ENTITIES.getKey(this.type);
        data.putString("type", (key == null ? Bubbles.NORMAL.getId() : key).toString());
        return data;
    }

    private void clearEffects() {
        for (StatusEffectInstance activeEffect : new HashSet<>(activeEffects)) {
            removeEffect(activeEffect);
        }
    }

    /**
     * Set whether the entity is able to move.
     * @param mobile the mobility to set (aka ability to move)
     */
    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    /**
     * Get whether the entity is able to move.
     * @return the mobility of the entity.
     */
    public boolean isMobile() {
        return mobile;
    }

    /**
     * Get the ability tag for an entity.
     * @param type the ability type.
     * @return the data tag (can be null if it wasn't created yet)
     */
    public MapType getAbilityTag(AbilityType<?> type) {
        return this.abilities.get(type);
    }

    /**
     * Get or create the ability tag for an ability.
     * @param type the ability type.
     * @return the data tag.
     */
    public MapType getOrCreateAbilityTag(AbilityType<?> type) {
        return this.abilities.computeIfAbsent(type, $ -> new MapType());
    }

    /**
     * Get persistent data tag.
     * @return the data tag.
     */
    public MapType getTag() {
        return tag;
    }

    /**
     * Get the attribute container for the entity.
     * @return the attribute container.
     */
    public final AttributeContainer getAttributes() {
        return attributes;
    }

    /**
     * Get the entity's scale.
     * @return the scale of the entity.
     */
    public float getScale() {
        return scale;
    }

    /**
     * Set the entity's scale.
     * @param scale the scale to set.
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Get the current x position of the entity.
     * @return the x position.
     */
    public float getX() {
        return x;
    }

    /**
     * Set the current x position of the entity.
     * @param x the x position.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Get the current y position of the entity.
     * @return the y position.
     */
    public float getY() {
        return y;
    }

    /**
     * Set the current y position of the entity.
     * @param y the y position.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Get the entity's rotation in degrees.
     *
     * @return the rotation of the entity.
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Set the entity's rotation in degrees.
     * @param rotation the rotation to set.
     */
    public void setRotation(float rotation) {
        this.rotation = rotation % 360;
    }

    /**
     * Get whether the entity is spawned.
     * @return true if spawned.
     */
    public final boolean isSpawned() {
        return spawned;
    }

    /**
     * Get whether the entity isn't spawned.
     * @return true if not spawned.
     */
    public final boolean isNotSpawned() {
        return !spawned;
    }

    /**
     * Get the environment where the entity was spawned in.
     * @return the spawn environment.
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Get whether the entity will be deleted soon.
     * @return true if it will be deleted soon!
     */
    public final boolean willBeDeleted() {
        return willBeDeleted;
    }

    /**
     * Get whether the entity is visible.
     * @return true if visible, false if invisible.
     */
    public boolean isVisible() {
        Rectangle entityBounds = getBounds();
        return entityBounds.x + entityBounds.width >= 0 && entityBounds.y + entityBounds.height >= 0 &&
                entityBounds.x <= game.getWidth() && entityBounds.y <= game.getHeight();
    }

    /**
     * Mark an entity as collidable.
     * @param entity the entity.
     */
    public void markAsCollidable(Entity entity) {
        canCollideWith.add(entity.getType());
    }

    /**
     * Mark an entity as collidable.
     * @param entityType the type of entity.
     */
    public void markAsCollidable(EntityType<?> entityType) {
        canCollideWith.add(entityType);
    }

    /**
     * Mark an entity to be attackable.
     * @param entity the entity.
     */
    public void markAsAttackable(Entity entity) {
        canAttack.add(entity.getType());
    }

    /**
     * Mark an entity to be attackable.
     * @param entityType the type of entity.
     */
    public void markAsAttackable(EntityType<?> entityType) {
        canAttack.add(entityType);
    }

    /**
     * Mark an entity to be invulnerable to.
     * @param entity the entity.
     */
    public void markAsInvulnerableTo(Entity entity) {
        invulnerableTo.add(entity.getType());
    }

    /**
     * Mark an entity to be invulnerable to.
     * @param entityType the type of entity.
     */
    public void markAsInvulnerableTo(EntityType<?> entityType) {
        invulnerableTo.add(entityType);
    }

    /**
     * Get whether this entity is collidable with the given entity.
     * @param other the other entity.
     * @return true if collidable.
     */
    public final boolean isCollidableWith(Entity other) {
        return canCollideWith.contains(other.type);
    }

    /**
     * Get whether this entity is currently colliding with another entity.
     * @param entity the other entity.
     * @return true if in collision.
     */
    public boolean isCollidingWith(Entity entity) {
        return CollisionUtil.isColliding(this, entity);
    }

    /**
     * Get whether this entity does attack the given entity.
     * @param other the other entity.
     * @return true if it would attack when near, false if it doesn't.
     */
    public boolean doesAttack(Entity other) {
        return canAttack.contains(other.type);
    }

    /**
     * Get whether this entity can be attacked by the given entity.
     * @param other the other entity.
     * @return true if this entity can be attacked by it, false if invulnerable to it.
     */
    public boolean canBeAttackedBy(Entity other) {
        return !invulnerableTo.contains(other.type);
    }

    /**
     * Get the RNG instance for x spawn positions.
     * @return the RNG instance.
     */
    public Rng getXRng() {
        return xRng;
    }

    /**
     * Get the RNG instance for y spawn positions.
     * @return the RNG instance.
     */
    public Rng getYRng() {
        return yRng;
    }

    /**
     * Get the current target entity to move to.
     * @return the current entity's target.
     */
    public Entity getTarget() {
        return this.currentTarget;
    }

    /**
     * Set the current target entity to move to.
     * @param target the current entity's target.
     */
    public void setTarget(Entity target) {
        this.currentTarget = target;
    }

    /**
     * Get the size of the entity (counted as the complete size from one end to another end)
     * This is used for collision detection. The collision is meant for circle-shaped entities.
     * @return the entity collision size.
     */
    public abstract double size();

    /**
     * Get the distance to another entity.
     * @param entityB the other entity.
     * @return the distance between this entity and the other.
     */
    public final double distanceTo(Entity entityB) {
        return Math.sqrt(Math.pow(entityB.x - x, 2) + Math.pow(entityB.y - y, 2));
    }

    /**
     * Get the distance to a position.
     * @param pos the position.
     * @return the distance between this entity and the given position.
     */
    public double distanceTo(Vec2f pos) {
        return Math.sqrt(Math.pow(pos.x - x, 2) + Math.pow(pos.y - y, 2));
    }

    /**
     * Get the angle (in degrees) towards another entity.
     * @param target the other entity.
     * @return the angle towards the given entity.
     */
    public double getAngleTo(Entity target) {
        return Math.toDegrees(Math.atan2(target.y - y, target.x - x));
    }

    /**
     * Get the entity's speed.
     * @return the speed.
     */
    public float getSpeed() {
        return (float) getAttributes().get(Attribute.SPEED);
    }

    public boolean isBad() {
        return false;
    }

    public String getName() {
        return Language.translate(getId().location() + "/entity/names/" + getId().path());
    }

    public Identifier getId() {
        return getType().getId();
    }
}
