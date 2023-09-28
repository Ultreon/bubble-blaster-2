package com.ultreon.bubbles.entity.player;

import com.badlogic.gdx.math.*;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.config.Config;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.*;
import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.ability.AbilityContainer;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.Constants;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.init.AmmoTypes;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.item.collection.PlayerItemCollection;
import com.ultreon.bubbles.player.InputController;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.commons.time.TimeProcessor;
import com.ultreon.data.types.MapType;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Rectangle2D;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

/**
 * <p>These are the vertex coordinates:</p>
 * <br>
 * <code>
 * |- Middle point<br>
 * |<br>
 * 9876543210123456789<br>
 * --------------------,<br>
 * ....#*********#.....| -8<br>
 * ....*.........*.....| -7<br>
 * ....#***#.#***#.....| -6<br>
 * ........*.*.........| -5<br>
 * ..#*****#.#******#..| -4<br>
 * .*..................| -3<br>
 * #..................#| -2<br>
 * *..................*| -1<br>
 * *..................*| 0  // Middle point.<br>
 * *..................*| 1<br>
 * #..................#| 2<br>
 * .*................*.| 3<br>
 * ..#..............#..| 4<br>
 * </code>
 */
public class Player extends LivingEntity implements InputController {
    private static final float[] ARROW_VERTICES = {
            -5, 0,
            -10, -10,
            15, 0,
            -10, 10
    };
    private static final float RADIUS = 20;
    private static final float DRAG = 0.98f;

    private final Circle shipShape;
    private final Polygon arrowShape;
    private int invincibilityTicks;

    // Types
    private AmmoType currentAmmo = AmmoTypes.BASIC;

    // Motion (Arrow Keys).
    private boolean forward = false;
    private boolean backward = false;
    private boolean left = false;
    private boolean right = false;

    /**
     * Amount of degrees to rotate in a second.
     */
    private float rotationSpeed = 120f;

    // Delta velocity.
    private float velocityDelta = 0f;

    // Motion (XInput).
    private float joyStickX;
    private float joyStickY;

    // Normal values/
    private double score = 0.0;
    private int level = 1;

    // Modifiers.
    private long abilityEnergy;

    // Ability
    private final AbilityContainer abilityContainer = new AbilityContainer(this);
    private final PlayerItemCollection inventory = new PlayerItemCollection(this);
    private int shootCooldown;
    public final Vector2 tempVel = new Vector2();

    /**
     * Player entity.
     * The player is controlled be the keyboard and is one create the important features create the game. (Almost any game).
     *
     * @see LivingEntity
     */
    public Player(Environment environment) {
        super(Entities.PLAYER, environment);

        this.markAsCollidable(Entities.BUBBLE);

        this.shipShape = new Circle(-20, -20, 40);
        this.arrowShape = new Polygon(ARROW_VERTICES);

        this.velocity.setZero();

        this.attributes.setBase(Attribute.DEFENSE, 1f);
        this.attributes.setBase(Attribute.ATTACK, 0.75f);
        this.attributes.setBase(Attribute.MAX_HEALTH, 30f);
        this.attributes.setBase(Attribute.SPEED, 4f);
        this.attributes.setBase(Attribute.SCORE_MODIFIER, 1f);

        this.health = 30f;

        for (EntityType<?> entityType : Registries.ENTITIES.values()) {
            markAsAttackable(entityType);
        }

        this.invincibilityTicks = 40;
        this.invincible = true;
    }

    /**
     * @param s the message.
     */
    public void sendMessage(String s) {
        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame != null) {
            loadedGame.receiveMessage(s);
        }
    }

    /**
     * Prepare spawn
     * Called when the entity was spawned.
     *
     * @param information the data to spawn with.
     */
    @Override
    public void prepareSpawn(SpawnInformation information) {
        super.prepareSpawn(information);
        BubbleBlaster game = this.environment.game();
        Rectangle gameBounds = game.getGameBounds();
        this.pos.x = Mth.clamp(this.pos.x, gameBounds.getX(), gameBounds.getX() + gameBounds.getWidth());
        this.pos.y = Mth.clamp(this.pos.y, gameBounds.getY(), gameBounds.getY() + gameBounds.getHeight());
        this.make();
    }

    @Override
    protected void make() {
        this.valid = true;
    }

    @Override
    protected void invalidate() {
        this.valid = false;
    }

    @Override
    public Circle getShape() {
        return new Circle(this.pos.x - RADIUS * 2 * this.scale / 2, this.pos.y - RADIUS * 2 * this.scale / 2, RADIUS * 2 * this.scale);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.pos.x - RADIUS, this.pos.y - RADIUS, RADIUS * 2, RADIUS * 2);
    }

    private Circle transformShip(Circle ship) {
        ship.setPosition(this.pos);

        return ship;
    }

    private Polygon transformArrow(Polygon arrow) {
        arrow.setPosition(this.pos.x, this.pos.y);
        arrow.setRotation(this.rotation);

        return arrow;
    }

    /**
     * @return the shape create the ship.
     */
    public Circle getShipShape() {
        return this.transformShip(this.shipShape);
    }

    /**
     * @return the arrow shape create the ship.
     */
    public Polygon getArrowShape() {
        return this.transformArrow(this.arrowShape);
    }

    /**
     * @return the center position.
     */
    @SuppressWarnings("unused")
    public Vector2 getCenter() {
        return this.pos.cpy();
    }

    /**
     * Tick the player.
     *
     * @param environment the game-type where the entity is from.
     */
    @Override
    public void tick(Environment environment) {
        //***********************//
        // Spawn and load checks //
        //***********************//

        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();

        if (loadedGame == null) {
            return;
        }

        if (isNotSpawned()) return;

        //**************************//
        // Player component ticking //
        //**************************//
        this.abilityContainer.onEntityTick();
        this.inventory.tick();

        //****************//
        // Player motion. //
        //****************//
        float motion = 0.0f;
        float rotate = 0.0f;

        // Check each direction, to create velocity
        if (this.forward) motion += getSpeed();
        if (this.backward) motion -= getSpeed();
        if (this.left) rotate -= this.rotationSpeed;
        if (this.right) rotate += this.rotationSpeed;
        if (this.joyStickY != 0) motion = (float) (this.joyStickY * this.attributes.getBase(Attribute.SPEED));
        if (this.joyStickX != 0) rotate = this.joyStickX * this.rotationSpeed;

        // Update X, and Y.
        if (this.canMove) {
            this.rotation += rotate / TPS;
        }

        // Calculate Velocity X and Y.
        float angelRadians = this.rotation * MathUtils.degRad;
        float tempVelX = MathUtils.cos(angelRadians) * motion;
        float tempVelY = MathUtils.sin(angelRadians) * motion;
        this.tempVel.set(tempVelX, tempVelY);

        if (this.canMove) {
            this.accel.add(this.tempVel);
        }

        // Velocity on X-axis.
        if (this.velocity.x > 0) {
            if (this.velocity.x + this.velocityDelta < 0) {
                this.velocity.x = 0;
            } else {
                this.velocity.x += this.velocityDelta;
            }
        } else if (this.velocity.x < 0) {
            if (this.velocity.x + this.velocityDelta > 0) {
                this.velocity.x = 0;
            } else {
                this.velocity.x -= this.velocityDelta;
            }
        }

        // Velocity on Y-axis.
        if (this.velocity.y > 0) {
            if (this.velocity.y + this.velocityDelta < 0) {
                this.velocity.y = 0;
            } else {
                this.velocity.y += this.velocityDelta;
            }
        } else if (this.velocity.y < 0) {
            if (this.velocity.y + this.velocityDelta > 0) {
                this.velocity.y = 0;
            } else {
                this.velocity.y -= this.velocityDelta;
            }
        }

        // Game border collision.
        Rectangle2D gameBounds = loadedGame.getGamemode().getGameBounds();

        // Leveling up.
        if (this.score / BubbleBlasterConfig.LEVEL_THRESHOLD.get() > this.level) {
            levelUp();
            environment.onLevelUp(this, level);
        }

        // Shooting cooldown.
        shootCooldown = Math.max(shootCooldown - 1, 0);

        // Invincibility ticks after spawn.
        if (invincibilityTicks-- < 0) {
            invincibilityTicks = 0;
            invincible = false;
        }

        for (StatusEffectInstance appliedEffect : this.statusEffects) {
            appliedEffect.tick(this);
        }

        this.statusEffects.removeIf((effect -> effect.getRemainingTime().isNegative()));

        this.accel.x *= DRAG / TPS * 40;
        this.accel.y *= DRAG / TPS * 40;

        this.prevPos.set(this.pos);

        if (this.canMove) {
            this.pos.add((this.accel.x + this.velocity.x) / TPS, (this.accel.y + this.velocity.y) / TPS);
        }

        double minX = gameBounds.getMinX() + this.size() / 2;
        double minY = gameBounds.getMinY() + this.size() / 2;
        double maxX = gameBounds.getMaxX() - this.size() / 2;
        double maxY = gameBounds.getMaxY() - this.size() / 2;

        if (this.pos.x > maxX && this.velocity.x > 0) this.velocity.x = 0;
        if (this.pos.x < minX && this.velocity.x < 0) this.velocity.x = 0;
        if (this.pos.x > maxX && this.accel.x > 0) this.accel.x = 0;
        if (this.pos.x < minX && this.accel.x < 0) this.accel.x = 0;

        if (this.pos.y > maxY && this.velocity.y > 0) this.velocity.y = 0;
        if (this.pos.y < minY && this.velocity.y < 0) this.velocity.y = 0;
        if (this.pos.y > maxY && this.accel.y > 0) this.accel.y = 0;
        if (this.pos.y < minY && this.accel.y < 0) this.accel.y = 0;

        this.pos.x = (float) Mth.clamp(this.pos.x, minX, maxX);
        this.pos.y = (float) Mth.clamp(this.pos.y, minY, maxY);
    }

    @Override
    public void damage(double value, EntityDamageSource source) {
        double defense = attributes.getBase(Attribute.DEFENSE);
        if (defense <= 0.0d) {
            this.destroy();
            return;
        }

        // Deal damage to the player.
        this.health -= value / defense;

        // Check health.
        this.checkHealth();

        // Check if source has attack modifier.
        if (value > 0.0d) {
            // Check if window is not focused.
            if (!BubbleBlaster.getInstance().getGameWindow().isFocused()) {
                if (SystemUtils.IS_JAVA_9) {
                    // Let the taskbar icon flash. (Java 9+)
                    BubbleBlaster.getInstance().getGameWindow().requestUserAttention();
                }
            }
        }
    }

    @Override
    public void onCollision(Entity other, double deltaTime) {
        if (other.isBad()) return;

        // Modifiers
        if (other instanceof Bubble bubble) {
            AttributeContainer attributeMap = bubble.getAttributes();
            double scoreMultiplier = attributeMap.get(Attribute.SCORE);
            double attack = attributeMap.get(Attribute.ATTACK);  // Maybe used.
            double defense = attributeMap.get(Attribute.DEFENSE);  // Maybe used.

            // Attributes
            float radius = bubble.getRadius();
            float speed = bubble.getSpeed();

            // Calculate score value.
            double visibleValue = radius * speed;
            double nonVisibleValue = attack * defense;
            double scoreValue = ((visibleValue * (nonVisibleValue + 1)) * scoreMultiplier * scoreMultiplier) * getAttributes().get(Attribute.SCORE_MODIFIER) * deltaTime / Constants.BUBBLE_SCORE_REDUCTION_SELF;

            // Add score.
            addScore(scoreValue);
        } else if (other.getAttributes().has(Attribute.SCORE_MODIFIER)) {
            double score = other.getAttributes().get(Attribute.SCORE_MODIFIER);
            addScore(score);
        }
    }

    /**
     * Renders the player.
     *
     * @param renderer the renderer to use.
     */
    @Override
    public void render(Renderer renderer) {
        // Don't render if the player isn't spawned.
        if (this.isNotSpawned()) return;

        // Fill the ship with the correct color.
        renderer.setColor(Color.rgb(0xdc143c));
        renderer.circle(this.pos.x, this.pos.y, RADIUS * 2);

        // Fill the arrow with the correct color.
        renderer.setColor(Color.WHITE);
        renderer.polygon(getArrowShape());
    }

    /**
     * Rotate the player.
     *
     * @param deltaRotation amount of degrees to rotate.
     * @deprecated use {@link #rotate(float)} instead.
     */
    @Deprecated
    public void rotateDelta(int deltaRotation) {
        this.rotation += deltaRotation;
    }

    /**
     * Handles deleting of the player.
     *
     * @see #delete()
     */
    @Override
    public void onDelete() {
        this.invalidate();
    }

    /**
     * Checks health, if health is zero or less, it will trigger game-over.
     *
     * @see #getHealth()
     */
    @Override
    public void checkHealth() {
        if (health <= 0 && getEnvironment().isAlive()) {
            die();
        }
    }

    /**
     * Kills the player in an instant.
     */
    public void die() {
        if (getEnvironment().isAlive()) {
            getEnvironment().triggerGameOver();
        }
    }

    /**
     * Save the player data.
     *
     * @return the player data.
     */
    @Override
    public @NotNull MapType save() {
        @NotNull MapType document = super.save();

        document.putDouble("score", score);
        document.putFloat("rotation", rotation);
        return document;
    }

    /**
     * Load the player data.
     *
     * @param data the player data.
     */
    @Override
    public void load(MapType data) {
        super.save();

        this.score = data.getFloat("score");
        this.rotation = data.getFloat("rotation");
    }

    /**
     * Get the speed of the rotation.
     * The speed is in degrees per second.
     *
     * @return the rotation speed.
     */
    public float getRotationSpeed() {
        return rotationSpeed;
    }

    /**
     * Set the speed of the rotation.
     * The speed is in degrees per second.
     *
     * @param rotationSpeed the rotation speed to set.
     */
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    /**
     * Rotate the player.<br>
     * NOTE: Rotation is in degrees.
     *
     * @param rotation amount of degrees to rotate.
     */
    public void rotate(float rotation) {
        this.rotation = (this.rotation + rotation) % 360;
    }

    /**
     * Get the player's current score.
     *
     * @return the score of the player.
     */
    public double getScore() {
        return score;
    }

    /**
     * Increment score of the player.
     *
     * @param value amount of score to increment.
     */
    public void addScore(double value) {
        score += value;
    }

    /**
     * Decrement score of the player.
     *
     * @param value amount of score to decrement.
     */
    public void subtractScore(double value) {
        score -= value;
    }

    /**
     * Set the player's score.
     *
     * @param value the score of the player to set.
     */
    public void setScore(double value) {
        score = value;
    }

    /**
     * Get the player's current level.
     *
     * @return the level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Level-up the player.
     */
    public void levelUp() {
        level++;
    }

    /**
     * Decrement the player's level.
     */
    public void levelDown() {
        setLevel(getLevel() - 1);
    }

    /**
     * Set the player's level.
     *
     * @param level the level to set.
     */
    public void setLevel(int level) {
        this.level = Math.max(level, 1);
    }

    /**
     * Get the ability container create the entity.
     *
     * @return the requested {@link AbilityContainer}.
     * @see AbilityContainer
     */
    public AbilityContainer getAbilityContainer() {
        return abilityContainer;
    }

    /**
     * Get current ammo type.
     *
     * @return the current {@link AmmoType ammo type}.
     * @see AmmoType
     * @see #setCurrentAmmo(AmmoType)
     */
    public AmmoType getCurrentAmmo() {
        return currentAmmo;
    }

    /**
     * Set current ammo type.
     *
     * @param currentAmmo the ammo to set.
     * @see AmmoType
     * @see #getCurrentAmmo()
     */
    public void setCurrentAmmo(AmmoType currentAmmo) {
        this.currentAmmo = currentAmmo;
    }

    /**
     * Deactivate / activate the forwards motion.
     *
     * @param bool true to activate, false to deactivate.
     */
    public void forward(boolean bool) {
        this.forward = bool;
    }

    /**
     * Deactivate / activate the backwards motion.
     *
     * @param bool true to activate, false to deactivate.
     */
    public void backward(boolean bool) {
        this.backward = bool;
    }

    /**
     * Deactivate / activate the left rotation.
     *
     * @param bool true to activate, false to deactivate.
     */
    public void left(boolean bool) {
        this.left = bool;
    }

    /**
     * Deactivate / activate the right rotation.
     *
     * @param bool true to activate, false to deactivate.
     */
    public void right(boolean bool) {
        this.right = bool;
    }

    public float getVelocityDelta() {
        return velocityDelta;
    }

    public void setVelocityDelta(float velocityDelta) {
        this.velocityDelta = velocityDelta;
    }

    public long getAbilityEnergy() {
        return abilityEnergy;
    }

    public void setAbilityEnergy(long abilityEnergy) {
        this.abilityEnergy = abilityEnergy;
    }

    public float getJoyStickX() {
        return joyStickX;
    }

    public void setJoyStickX(float joyStickX) {
        this.joyStickX = joyStickX;
    }

    public float getJoyStickY() {
        return joyStickY;
    }

    public void setJoyStickY(float joyStickY) {
        this.joyStickY = joyStickY;
    }

    @Override
    public boolean hasAi() {
        return false;
    }

    @Override
    public double size() {
        return 40 * this.scale;
    }

    /**
     * Shoot a bullet from the {@linkplain #getCurrentAmmo() current ammo}.
     *
     * @see #setCurrentAmmo(AmmoType)
     */
    public void shoot() {
        shoot(false);
    }

    /**
     * Shoot a bullet from the {@linkplain #getCurrentAmmo() current ammo}.
     *
     * @see #setCurrentAmmo(AmmoType)
     */
    public void shoot(boolean force) {
        if (force || this.canShoot()) {
            shootCooldown = TimeProcessor.secondsToTicks(1.0);

            Vector2 bulletPos = this.pos.cpy().setAngleDeg(this.rotation).setLength(RADIUS / 2);
            Bullet bullet = new Bullet(this.currentAmmo, bulletPos, rotation, environment);
            bullet.setOwner(this);
            environment.spawn(bullet);
        }
    }

    public boolean canShoot() {
        return shootCooldown <= 0;
    }

    public int getInvincibilityTicks() {
        return invincibilityTicks;
    }

    @Override
    public String getName() {
        return "Player";
    }
}
