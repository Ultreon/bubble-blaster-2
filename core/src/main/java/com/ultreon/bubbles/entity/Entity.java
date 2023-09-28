package com.ultreon.bubbles.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GameObject;
import com.ultreon.bubbles.common.interfaces.StateHolder;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.ai.AiTask;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.player.ability.AbilityType;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.init.Bubbles;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.commons.util.CollisionUtil;
import com.ultreon.data.types.ListType;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.translations.v0.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected final Set<StatusEffectInstance> statusEffects = new CopyOnWriteArraySet<>();

    // Attributes
    protected float scale = 1f;
    protected AttributeContainer attributes = new AttributeContainer();
    protected AttributeContainer bases = new AttributeContainer();

    // Flags
    public boolean canMove = true;

    protected final Vector2 prevPos = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Vector2 accel = new Vector2();

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
    private Entity target;
    @Nullable
    protected AiTask currentAiTask;
    private final List<AiTask> aiTasks = new ArrayList<>();
    private boolean ghost = false;

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
        @Nullable Vector2 pos = information.getPos();
        if (pos != null) this.pos.set(pos);
    }

    /**
     * On spawn.
     *
     * @param pos         the position to spawn at.
     * @param environment te environment to spawn in.
     */
    public void onSpawn(Vector2 pos, Environment environment) {
        spawned = true;
    }

    /**
     * Handle when the entity was teleported.
     * To cancel teleport see {@link #onTeleporting(Vector2, Vector2)}
     *
     * @param from teleport origin.
     * @param to teleport destination.
     */
    public void onTeleported(Vector2 from, Vector2 to) {

    }

    /**
     * Handle teleporting before it happens. Can be cancelled by returning true.
     *
     * @param from teleport origin.
     * @param to teleport destination.
     * @return true to cancel, false to pass.
     */
    public boolean onTeleporting(Vector2 from, Vector2 to) {
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
        if (velocityX == 0 && velocityY == 0) return;

        this.accel.add(velocityX, velocityY);
    }

    private void setAcceleration(float x, float y) {
        this.accel.set(x, y);
    }

    /**
     * Trigger a Reflection
     * Triggers a reflection, there are some problems with the velocity.
     * That's why it's currently in beta.
     *
     * @param velocity the amount velocity for bounce.
     * @param delta    the delta change.
     */
    public void applyForce(Vector2 velocity, float delta) {
        this.applyForce(velocity.x, velocity.y, delta);
    }

    /**
     * Apply a force towards a direction.
     *
     * @param direction the direction (in degrees).
     * @param velocity the amount velocity for bounce.
     * @param delta    the delta change.
     */
    public void applyForceDir(float direction, float velocity, float delta) {
        if (velocity == 0) return;

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
        if (velocity == 0) return;
        this.applyForceDir((float) Math.toDegrees(Math.atan2(source.getY() - pos.y, source.getX() - pos.x)), velocity, delta);
    }

    public void accelerate(float amount) {
        // Calculate Velocity X and Y.
        float accelerateX = 0;
        float accelerateY = 0;
        if (this.canMove) {
            accelerateX += MathUtils.cos(this.getRotation() * MathUtils.degRad) * amount;
            accelerateY += MathUtils.sin(this.getRotation() * MathUtils.degRad) * amount;
        }

        // Set velocity X and Y.
        this.setAccelerateX(this.getAccelerateX() + accelerateX);
        this.setAccelerateY(this.getAccelerateY() + accelerateY);
    }
    
    /**
     * @return the x acceleration.
     */
    public float getAccelerateX() {
        return accel.x;
    }

    /**
     * @return the y acceleration.
     */
    public float getAccelerateY() {
        return accel.y;
    }

    /**
     * @param accelerateX the x acceleration to set.
     */
    public void setAccelerateX(float accelerateX) {
        this.accel.x = accelerateX;
    }

    /**
     * @param accelerateY the y acceleration to set.
     */
    public void setAccelerateY(float accelerateY) {
        this.accel.y = accelerateY;
    }

    /**
     * Tick event.
     *
     * @param environment the environment where the entity is from.
     */
    public void tick(Environment environment) {
        for (StatusEffectInstance effect : this.statusEffects) {
            effect.tick(this);
        }

        this.statusEffects.removeIf(effect -> effect.getRemainingTime().isNegative());

        this.accel.scl(0.98f * (float) TPS * 20 + 1);

        // Calculate Velocity X and Y.
        float angelRadians = this.getRotation() * MathUtils.degRad;
        this.velocity.setAngleRad(angelRadians).setLength(this.getSpeed());
        this.velocity.x = (float) (Math.cos(angelRadians) * this.getSpeed());
        this.velocity.y = (float) (Math.sin(angelRadians) * this.getSpeed());

        this.prevPos.set(this.pos);

        if (this.canMove)
            this.pos.add(
                    this.accel.x + this.velocity.x / TPS,
                    this.accel.y + this.velocity.y / TPS
            );

        if (hasAi()) {
            nextAiTask();

            if (target != null) {
                if (!target.isValid()) {
                    target = null;
                }
            }

            if (target != null) {
                float angleTo = this.getAngleToTarget();
                setRotation(angleTo);
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

    public EntityType<?> getType() {
        return type;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Teleport and Position     //
    ///////////////////////////////////
    public final void teleport(float x, float y) {
        this.teleport(new Vector2(x, y));
    }

    public final void teleport(Vector2 dest) {
        Vector2 old = pos.cpy();
        if (this.onTeleporting(old, dest)) return;
        this.pos.set(dest);
        this.onTeleported(old, dest.cpy());
    }

    //****************//
    //     Motion     //
    //****************//
    public void move(float deltaX, float deltaY) {
        this.pos.add(deltaX, deltaY);
    }

    public void move(Vector2 delta) {
        this.pos.add(delta);
    }

    public Vector2 getVelocity() {
        return velocity.cpy();
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    public void velocity(float x, float y) {
        this.velocity.set(x, y);
    }

    /**
     * Convert this entity to a simple string used for logging.
     * @return the simple string.
     * @see #toAdvancedString()
     */
    public String toSimpleString() {
        return getId() + "@(" + Math.round(getX()) + "," + Math.round(getY()) + ")";
    }

    /**
     * Convert this entity to an advanced string used for debugging / development.
     * @return the advanced string.
     * @see #toSimpleString()
     */
    public final String toAdvancedString() {
        @NotNull MapType nbt = save();
        String data = nbt.toString();

        return getId() + ":" + data;
    }

    /**
     * Get the active status effects.
     *
     * @return the active effects.
     */
    public Set<StatusEffectInstance> getActiveEffects() {
        return statusEffects;
    }

    /**
     * Add an effect to the entity.
     * @param appliedEffect the applied effect to add.
     */
    public void addEffect(StatusEffectInstance appliedEffect) {
        for (StatusEffectInstance appliedEffect1 : statusEffects) {
            if (appliedEffect1.getType() == appliedEffect.getType()) {
                if (appliedEffect1.getRemainingTime().toMillis() < appliedEffect.getRemainingTime().toMillis()) {
                    appliedEffect1.setRemainingTime(appliedEffect.getRemainingTime());
                }
                return;
            }
        }
        statusEffects.add(appliedEffect);
        appliedEffect.start(this);
    }

    /**
     * Remove a status effect from the entity.
     * @param appliedEffect the applied status effect.
     */
    public void removeEffect(StatusEffectInstance appliedEffect) {
        statusEffects.remove(appliedEffect);
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
        this.pos.x = positionTag.getFloat("x");
        this.pos.y = positionTag.getFloat("y");

        MapType acceleration = data.getMap("Acceleration");
        this.accel.x = acceleration.getFloat("x");
        this.accel.y = acceleration.getFloat("y");

        MapType previousTag = data.getMap("PrevPosition");
        this.prevPos.x = previousTag.getFloat("x");
        this.prevPos.y = previousTag.getFloat("y");

        MapType velocityTag = data.getMap("Velocity");
        this.velocity.x = velocityTag.getFloat("x");
        this.velocity.y = velocityTag.getFloat("y");

        ListType<MapType> activeEffectsData = data.getList("ActiveEffects");
        clearEffects();
        for (var activeEffectData : activeEffectsData) {
            this.statusEffects.add(StatusEffectInstance.load(activeEffectData));
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
        positionTag.putFloat("x", this.pos.x);
        positionTag.putFloat("y", this.pos.y);
        data.put("Position", positionTag);

        var previousTag = new MapType();
        previousTag.putDouble("x", this.prevPos.x);
        previousTag.putDouble("y", this.prevPos.y);
        data.put("PrevPosition", previousTag);

        // Velocity.
        var velocityTag = new MapType();
        velocityTag.putDouble("x", this.velocity.x);
        velocityTag.putDouble("y", this.velocity.y);
        data.put("Velocity", velocityTag);

        var activeEffectsTag = new ListType<MapType>();
        for (var instance : statusEffects)
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
        for (StatusEffectInstance activeEffect : new HashSet<>(statusEffects)) {
            removeEffect(activeEffect);
        }
    }

    /**
     * Set whether the entity is able to move.
     * @param canMove the mobility to set (aka ability to move)
     */
    @Deprecated
    public void setMobile(boolean canMove) {
        this.canMove = canMove;
    }

    /**
     * Get whether the entity is able to move.
     * @return the mobility of the entity.
     */
    @Deprecated
    public boolean isMobile() {
        return canMove;
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

    public boolean isGhost() {
        return ghost;
    }

    public void setGhost(boolean ghost) {
        this.ghost = ghost;
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
        return this.canAttack.contains(other.type);
    }

    /**
     * Get whether this entity can be attacked by the given entity.
     * @param other the other entity.
     * @return true if this entity can be attacked by it, false if invulnerable to it.
     */
    public boolean canBeAttackedBy(Entity other) {
        if (this.ghost) return false;
        return !this.invulnerableTo.contains(other.type);
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
        return this.target;
    }

    /**
     * Set the current target entity to move to.
     * @param target the current entity's target.
     */
    public void setTarget(Entity target) {
        this.target = target;
    }

    /**
     * Get the size of the entity (counted as the complete size from one end to another end)
     * This is used for collision detection. The collision is meant for circle-shaped entities.
     * @return the entity collision size.
     */
    public abstract double size();

    public float getDistanceToTarget() {
        return this.pos.dst(this.target.pos);
    }

    public float getAngleToTarget() {
        return this.getAngleTo(this.target);
    }

    /**
     * Get the distance to another entity.
     * @param entityB the other entity.
     * @return the distance between this entity and the other.
     */
    public final double distanceTo(Entity entityB) {
        return this.pos.dst(entityB.pos);
    }

    /**
     * Get the distance to a position.
     * @param pos the position.
     * @return the distance between this entity and the given position.
     */
    public double distanceTo(Vector2 pos) {
        return this.pos.dst(pos);
    }

    /**
     * Get the angle (in degrees) towards another entity.
     * @param target the other entity.
     * @return the angle towards the given entity.
     */
    public float getAngleTo(Entity target) {
        return (float) Math.toDegrees(Math.atan2(target.pos.y - this.pos.y, target.pos.x - this.pos.x));
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

    @Override
    public String toString() {
        return "Entity[" + getId() +
                "]#" + entityId +
                "@" + uniqueId;
    }
}
