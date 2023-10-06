package com.ultreon.bubbles.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GameObject;
import com.ultreon.bubbles.common.interfaces.StateHolder;
import com.ultreon.bubbles.effect.StatusEffect;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.ai.AiTask;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.player.ability.AbilityType;
import com.ultreon.bubbles.entity.spawning.NaturalSpawnReason;
import com.ultreon.bubbles.entity.spawning.SpawnInformation;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.event.v1.EffectEvents;
import com.ultreon.bubbles.init.BubbleTypes;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.random.JavaRandom;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.util.CollisionUtil;
import com.ultreon.data.types.ListType;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.translations.v1.Language;
import org.jetbrains.annotations.ApiStatus;
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
public abstract class Entity extends GameObject implements StateHolder {
    public static final Entity UNDEFINED = Entities.UNDEFINED_TYPE.create(null);

    // ID's
    protected long id = -1L;
    private UUID uuid;

    // Types
    protected final EntityType<?> type;

    // Misc
    protected Shape2D shape;
    protected final HashSet<EntityType<?>> canCollideWith = new HashSet<>();
    protected final HashSet<EntityType<?>> canAttack = new HashSet<>();
    protected final HashSet<EntityType<?>> invulnerableTo = new HashSet<>();
    protected final Set<StatusEffectInstance> statusEffects = new CopyOnWriteArraySet<>();

    // Attributes
    protected float scale = 1f;
    protected final AttributeContainer attributes = new AttributeContainer();
    protected AttributeContainer bases = new AttributeContainer();

    // Flags
    public boolean canMove = true;

    protected final Vector2 prevPos = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Vector2 accel = new Vector2();

    protected boolean valid;
    private boolean spawned;

    @NotNull
    protected final World world;

    // Tag
    private MapType tag = new MapType();

    // Fields.
    protected float rotation = 0;

    // Abilities.
    private final HashMap<AbilityType<?>, MapType> abilities = new HashMap<>();
    private boolean willBeDeleted;
    private final RandomSource random = new JavaRandom();
    protected final BubbleBlaster game = BubbleBlaster.getInstance();
    private Entity target;
    @Nullable
    protected AiTask currentAiTask;
    private final List<AiTask> aiTasks = new ArrayList<>();
    private boolean ghost = false;

    /**
     * The entity constructor.
     * @param type the type of entity to use.
     * @param world the world where it would spawn in.
     */
    public Entity(EntityType<?> type, @NotNull World world) {
        this.world = world;
        this.type = type;
    }

    @ApiStatus.Internal
    public void setId(long id) {
        this.id = id;
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
        return Collections.unmodifiableList(this.aiTasks);
    }

    /**
     * Get entity uuid.
     *
     * @return the entity's uuid.
     */
    public UUID getUuid() {
        return this.uuid;
    }

    @ApiStatus.Internal
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Get entity id.
     *
     * @return the entity's id.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Prepare spawning of the entity.
     * @param information the spawn information
     */
    public void preSpawn(SpawnInformation information) {
        @Nullable Vector2 spawnPos = this.pos;
        if (information.getReason() instanceof NaturalSpawnReason) {
            NaturalSpawnReason reason = (NaturalSpawnReason) information.getReason();
            spawnPos.set(information.getWorld().getGamemode().getSpawnPos(this, information.getPos(), reason.getUsage(), information.getRandom(), reason.getRetry()));
        }

        if (information.getPos() != null)
            spawnPos = information.getPos();

        if (spawnPos != null)
            this.pos.set(spawnPos);
    }

    /**
     * On spawn.
     *
     * @param pos         the position to spawn at.
     * @param world te world to spawn in.
     */
    public void onSpawn(Vector2 pos, World world) {
        this.spawned = true;
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
     *
     * @param other     the other entity.
     * @param deltaTime the delta time from the check before this one. (This is for managing correct attack damage for example)
     */
    public void onCollision(Entity other, double deltaTime) {

    }

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
        return this.valid;
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
        this.applyForceDir(source.getAngleTo(this), velocity, delta);
    }

    public void accelerate(float amount, boolean useVelocity) {
        // Calculate Velocity X and Y.
        float accelerateX = 0;
        float accelerateY = 0;
        if (this.canMove) {
            accelerateX += MathUtils.cos(this.getRotation() * MathUtils.degRad) * amount;
            accelerateY += MathUtils.sin(this.getRotation() * MathUtils.degRad) * amount;
        }

        // Set velocity X and Y.
        (useVelocity ? this.velocity : this.accel).add(accelerateX, accelerateY);
    }
    
    /**
     * @return the x acceleration.
     */
    public float getAccelerateX() {
        return this.accel.x;
    }

    /**
     * @return the y acceleration.
     */
    public float getAccelerateY() {
        return this.accel.y;
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
     * @param world the world where the entity is from.
     */
    public void tick(World world) {
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

        if (this.hasAi()) {
            this.nextAiTask();

            if (this.target != null) {
                if (!this.target.isValid()) {
                    this.target = null;
                }
            }

            if (this.target != null) {
                float angleTo = this.getAngleToTarget();
                this.setRotation(angleTo);
            }
        }
    }

    /**
     * Execute and get the new AI-task.
     * @return the new AI-task.
     */
    @SuppressWarnings("UnusedReturnValue")
    public AiTask nextAiTask() {
        for (AiTask task : this.aiTasks) {
            if (task.executeTask(this)) {
                return this.currentAiTask = task;
            }
        }
        return this.currentAiTask = null;
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
        if (this.isValid()) {
            this.invalidate();
            this.valid = false;
        }

        this.onDelete();

        this.willBeDeleted = true;
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
        if (o == null || this.getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return this.id == entity.id;
    }

    protected void make() {

    }

    protected void invalidate() {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Get Bounds     //
    ////////////////////////
    public abstract Rectangle getBounds();

    public EntityType<?> getType() {
        return this.type;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Teleport and Position     //
    ///////////////////////////////////
    public final void teleport(float x, float y) {
        this.teleport(new Vector2(x, y));
    }

    public final void teleport(Vector2 dest) {
        Vector2 old = this.pos.cpy();
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
        return this.velocity.cpy();
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
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
        return this.getKey() + "@(" + Math.round(this.getX()) + "," + Math.round(this.getY()) + ")";
    }

    /**
     * Convert this entity to an advanced string used for debugging / development.
     * @return the advanced string.
     * @see #toSimpleString()
     */
    public final String toAdvancedString() {
        @NotNull MapType nbt = this.save();
        String data = nbt.toString();

        return this.getKey() + ":" + data;
    }

    /**
     * Get the active status effects.
     *
     * @return the active effects.
     */
    public Set<StatusEffectInstance> getActiveEffects() {
        return this.statusEffects;
    }

    /**
     * Add an effect to the entity.
     * @param instance the applied effect to add.
     */
    public void addEffect(StatusEffectInstance instance) {
        for (StatusEffectInstance effectInstance : this.statusEffects) {
            if (effectInstance.getType() == instance.getType()) {
                if (effectInstance.getRemainingTime().toMillis() < instance.getRemainingTime().toMillis()) {
                    EffectEvents.UPDATE.factory().onUpdate(effectInstance);
                    effectInstance.setRemainingTime(instance.getRemainingTime());
                }
                return;
            }
        }
        if (EffectEvents.GAIN.factory().onGain(instance).isCanceled()) return;
        instance.start(this);
        this.statusEffects.add(instance);
    }

    /**
     * Remove a status effect from the entity.
     * @param instance the applied status effect.
     */
    public void removeEffect(StatusEffectInstance instance) {
        if (this.statusEffects.contains(instance)) {
            if (EffectEvents.LOSE.factory().onLose(instance).isCanceled()) return;
            this.statusEffects.remove(instance);
            if (instance.isActive()) {
                instance.stop(this);
            }
        }
    }

    /**
     * Load an entity without having the entity type.
     * This will load the type of entity too.
     *
     * @param world the world to load it in.
     * @param tags the nbt data.
     * @return the loaded entity.
     */
    @Nullable
    public static Entity loadFully(World world, MapType tags) {
        Identifier type = Identifier.tryParse(tags.getString("type"));
        if (type == null) return null;
        EntityType<?> entityType = Registries.ENTITIES.getValue(type);
        if (entityType == null) {
            BubbleBlaster.LOGGER.warn("Unknown entity loaded: " + type);
            return null;
        }
        return entityType.create(world, tags);
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
        this.clearEffects();
        for (MapType activeEffectData : activeEffectsData) {
            this.statusEffects.add(StatusEffectInstance.load(activeEffectData));
        }

        this.id = data.getLong("id");
        this.uuid = data.getUUID("uuid");
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
        MapType data = new MapType();
        data.put("Tag", this.tag);
        data.put("Attributes", this.attributes.save());
        data.put("AttributesModifiers", this.attributes.saveModifiers());

        // Save position.
        MapType positionTag = new MapType();
        positionTag.putFloat("x", this.pos.x);
        positionTag.putFloat("y", this.pos.y);
        data.put("Position", positionTag);

        MapType previousTag = new MapType();
        previousTag.putDouble("x", this.prevPos.x);
        previousTag.putDouble("y", this.prevPos.y);
        data.put("PrevPosition", previousTag);

        // Velocity.
        MapType velocityTag = new MapType();
        velocityTag.putDouble("x", this.velocity.x);
        velocityTag.putDouble("y", this.velocity.y);
        data.put("Velocity", velocityTag);

        ListType<MapType> activeEffectsTag = new ListType<>();
        for (StatusEffectInstance instance : this.statusEffects)
            activeEffectsTag.add(instance.save());
        data.put("ActiveEffects", activeEffectsTag);

        // Other properties.
        data.putLong("id", this.id);
        if (this.uuid != null) {
            data.putUUID("uuid", this.uuid);
        }
        data.putDouble("scale", this.scale);
        data.putFloat("rotation", this.rotation);

        Identifier key = Registries.ENTITIES.getKey(this.type);
        data.putString("type", (key == null ? BubbleTypes.NORMAL.getId() : key).toString());
        return data;
    }

    private void clearEffects() {

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
        return this.canMove;
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
        return this.tag;
    }

    /**
     * Get the attribute container for the entity.
     * @return the attribute container.
     */
    public final AttributeContainer getAttributes() {
        return this.attributes;
    }

    /**
     * Get the entity's scale.
     * @return the scale of the entity.
     */
    public float getScale() {
        return this.scale;
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
        return this.rotation;
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
        return this.spawned;
    }

    /**
     * Get whether the entity isn't spawned.
     * @return true if not spawned.
     */
    public final boolean isNotSpawned() {
        return !this.spawned;
    }

    /**
     * Get the world where the entity was spawned in.
     * @return the spawn world.
     */
    public @NotNull World getWorld() {
        return this.world;
    }

    /**
     * Get whether the entity will be deleted soon.
     * @return true if it will be deleted soon!
     */
    public final boolean willBeDeleted() {
        return this.willBeDeleted;
    }

    public boolean isGhost() {
        return this.ghost;
    }

    public void setGhost(boolean ghost) {
        this.ghost = ghost;
    }

    /**
     * Get whether the entity is visible.
     * @return true if visible, false if invisible.
     */
    public boolean isVisible() {
        Rectangle entityBounds = this.getBounds();
        return entityBounds.x + entityBounds.width >= 0 && entityBounds.y + entityBounds.height >= 0 &&
                entityBounds.x <= this.game.getWidth() && entityBounds.y <= this.game.getHeight();
    }

    /**
     * Mark an entity as collidable.
     * @param entity the entity.
     */
    public void markAsCollidable(Entity entity) {
        this.canCollideWith.add(entity.getType());
    }

    /**
     * Mark an entity as collidable.
     * @param entityType the type of entity.
     */
    public void markAsCollidable(EntityType<?> entityType) {
        this.canCollideWith.add(entityType);
    }

    /**
     * Mark an entity to be attackable.
     * @param entity the entity.
     */
    public void markAsAttackable(Entity entity) {
        this.canAttack.add(entity.getType());
    }

    /**
     * Mark an entity to be attackable.
     * @param entityType the type of entity.
     */
    public void markAsAttackable(EntityType<?> entityType) {
        this.canAttack.add(entityType);
    }

    /**
     * Mark an entity to be invulnerable to.
     * @param entity the entity.
     */
    public void markAsInvulnerableTo(Entity entity) {
        this.invulnerableTo.add(entity.getType());
    }

    /**
     * Mark an entity to be invulnerable to.
     * @param entityType the type of entity.
     */
    public void markAsInvulnerableTo(EntityType<?> entityType) {
        this.invulnerableTo.add(entityType);
    }

    /**
     * Get whether this entity is collidable with the given entity.
     * @param other the other entity.
     * @return true if collidable.
     */
    public final boolean isCollidableWith(Entity other) {
        return this.canCollideWith.contains(other.type);
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
     *
     * @return the entity collision size.
     */
    public abstract float radius();

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
        return this.pos.cpy().dst(entityB.pos);
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
     * Get the angle (in degrees) towards another entity.
     * @param target the other entity.
     * @return the angle towards the given entity.
     */
    public float getAngleTo(Vector2 target) {
        return (float) Math.toDegrees(Math.atan2(target.y - this.pos.y, target.x - this.pos.x));
    }

    /**
     * Get the entity's speed.
     * @return the speed.
     */
    public float getSpeed() {
        return (float) this.getAttributes().get(Attribute.SPEED);
    }

    public boolean isBad() {
        return false;
    }

    public String getName() {
        return Language.translate(this.getKey().location() + "/entity/names/" + this.getKey().path());
    }

    public Identifier getKey() {
        return this.getType().getKey();
    }

    @Override
    public String toString() {
        return "Entity[" + this.getKey() +
                "]#" + this.id +
                "@" + this.uuid;
    }

    @Nullable
    public StatusEffectInstance getActiveEffect(StatusEffect type) {
        return this.statusEffects.stream().filter(statusEffectInstance -> statusEffectInstance.getType() == type).findAny().orElse(null);
    }
}
