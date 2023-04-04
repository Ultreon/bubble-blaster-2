package com.ultreon.bubbles.entity.player;

import com.ultreon.bubbles.common.PolygonBuilder;
import com.ultreon.bubbles.entity.Bullet;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.LivingEntity;
import com.ultreon.bubbles.entity.SpawnInformation;
import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.ability.AbilityContainer;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.Constants;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.init.AmmoTypes;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.item.collection.PlayerItemCollection;
import com.ultreon.bubbles.player.InputController;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.MessengerScreen;
import com.ultreon.bubbles.render.screen.Screen;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.commons.time.TimeProcessor;
import net.querz.nbt.tag.CompoundTag;
import org.apache.commons.lang3.SystemUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.*;
import java.awt.geom.*;
import java.util.Objects;

import static com.ultreon.bubbles.game.BubbleBlaster.TPS;

/**
 * <p>These are the vertex coordinates:</p>
 * <br>
 * <code>
 *     |- Middle point<br>
 *     |<br>
 *     9876543210123456789<br>
 *     --------------------,<br>
 *     ....#*********#.....| -8<br>
 *     ....*.........*.....| -7<br>
 *     ....#***#.#***#.....| -6<br>
 *     ........*.*.........| -5<br>
 *     ..#*****#.#******#..| -4<br>
 *     .*..................| -3<br>
 *     #..................#| -2<br>
 *     *..................*| -1<br>
 *     *..................*| 0  // Middle point.<br>
 *     *..................*| 1<br>
 *     #..................#| 2<br>
 *     .*................*.| 3<br>
 *     ..#..............#..| 4<br>
 * </code>
 */
public class Player extends LivingEntity implements InputController {
    /*
    */

    private final Area shipShape;
    private final Area arrowShape;
    private int invincibilityTicks;

    // Types
    private AmmoType currentAmmo = AmmoTypes.BASIC.get();

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
    private double velocityDelta;

    // Motion (XInput).
    private float joyStickX;
    private float joyStickY;

    // Normal values/
    private double score = 0d;
    private int level = 1;

    // Modifiers.
    private long abilityEnergy;

    // Ability
    private final AbilityContainer abilityContainer = new AbilityContainer(this);

    private double accelerateX = 0.0d;
    private double accelerateY = 0.0d;
    private final PlayerItemCollection inventory = new PlayerItemCollection(this);
    private int shootCooldown;

    /**
     * Player entity.
     * The player is controlled be the keyboard and is one create the important features create the game. (Almost any game).
     *
     * @see LivingEntity
     */
    public Player(Environment environment) {
        super(Entities.PLAYER.get(), environment);

        this.markAsCollidable(Entities.BUBBLE.get());

        // Ship shape.
        Ellipse2D shipShape1 = new Ellipse2D.Double(-20, -20, 40, 40);
        this.shipShape = new Area(shipShape1);

        // Arrow shape.
        Polygon arrowShape1 = new PolygonBuilder()
                .add(-5, 0)
                .add(-10, -10)
                .add(15, 0)
                .add(-10, 10)
                .build();
        this.arrowShape = new Area(arrowShape1);

        // Velocity.
        this.velX = 0;
        this.velY = 0;

        // Set attributes.
        this.attributes.setBase(Attribute.DEFENSE, 1f);
        this.attributes.setBase(Attribute.ATTACK, 0.75f);
        this.attributes.setBase(Attribute.MAX_HEALTH, 30f);
        this.attributes.setBase(Attribute.SPEED, 16f);
        this.attributes.setBase(Attribute.SCORE_MODIFIER, 1f);

        // Health
        this.health = 30f;

        for (EntityType<?> entityType : Registry.ENTITIES.values()) {
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
        @Nullable Screen currentScene = Objects.requireNonNull(Util.getSceneManager()).getCurrentScreen();
        if ((currentScene == null && BubbleBlaster.getInstance().isInGame()) ||
                currentScene instanceof MessengerScreen) {
            Rectangle2D gameBounds = environment.game().getGameBounds();
            this.x = (float) MathHelper.clamp(x, gameBounds.getMinX(), gameBounds.getMaxX());
            this.y = (float) MathHelper.clamp(y, gameBounds.getMinY(), gameBounds.getMaxY());
            make();
        }
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
    protected boolean isValid() {
        return this.valid;
    }

    @Override
    public Ellipse2D getShape() {
        return new Ellipse2D.Double(x - 40 * scale / 2, y - 40 * scale / 2, 40 * scale, 40 * scale);
    }

    @NonNull
    private Area transformArea(Area shipShape) {
        Area area = new Area(shipShape);
        AffineTransform transform = new AffineTransform();
        transform.scale(scale, scale);
        transform.translate(x, y);

        area.transform(transform);

        return area;
    }

    @NonNull
    private Area transformAreaRotated(Area shipShape) {
        Area area = new Area(shipShape);
        AffineTransform transform = new AffineTransform();
        transform.scale(scale, scale);
        transform.translate(x, y);
        transform.rotate(Math.toRadians(rotation));

        area.transform(transform);

        return area;
    }

    /**
     * @return the x acceleration.
     */
    public double getAccelerateX() {
        return accelerateX;
    }

    /**
     * @return the y acceleration.
     */
    public double getAccelerateY() {
        return accelerateY;
    }

    /**
     * @param accelerateX the x acceleration to set.
     */
    public void setAccelerateX(double accelerateX) {
        this.accelerateX = accelerateX;
    }

    /**
     * @param accelerateY the y acceleration to set.
     */
    public void setAccelerateY(double accelerateY) {
        this.accelerateY = accelerateY;
    }

    /**
     * @return the shape create the ship.
     */
    public Area getShipArea() {
        return transformArea(shipShape);
    }

    /**
     * @return the arrow shape create the ship.
     */
    public Area getArrowArea() {
        return transformAreaRotated(arrowShape);
    }

    /**
     * @return the center position.
     */
    @SuppressWarnings("unused")
    public Point getCenter() {
        return new Point((int) getBounds().getCenterX(), (int) getBounds().getCenterY());
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

        this.accelerateX = accelerateX / ((0.05 / (1 * (double) TPS / 20)) + 1);
        this.accelerateY = accelerateY / ((0.05 / (1 * (double) TPS / 20)) + 1);

        double motion = 0.0f;
        double rotate = 0.0f;

        // Check each direction, to create velocity
        if (this.forward) motion += this.attributes.getBase(Attribute.SPEED) * 10;
        if (this.backward) motion -= this.attributes.getBase(Attribute.SPEED) * 10;
        if (this.left) rotate -= this.rotationSpeed;
        if (this.right) rotate += this.rotationSpeed;
        if (this.joyStickY != 0) motion = this.joyStickY * this.attributes.getBase(Attribute.SPEED);
        if (this.joyStickX != 0) rotate = this.joyStickX * this.rotationSpeed;

        // Update X, and Y.
        if (isMobile()) {
            this.rotation += rotate / TPS;
        }

        // Calculate Velocity X and Y.
        double angelRadians = Math.toRadians(this.rotation);
        double tempVelX = Math.cos(angelRadians) * motion;
        double tempVelY = Math.sin(angelRadians) * motion;

        if (isMobile()) {
            this.accelerateX += tempVelX / ((double) TPS);
            this.accelerateY += tempVelY / ((double) TPS);
        }

        // Velocity on X-axis.
        if (this.velX > 0) {
            if (this.velX + this.velocityDelta < 0) {
                this.velX = 0;
            } else {
                this.velX += this.velocityDelta;
            }
        } else if (this.velX < 0) {
            if (this.velX + this.velocityDelta > 0) {
                this.velX = 0;
            } else {
                this.velX -= this.velocityDelta;
            }
        }

        // Velocity on Y-axis.
        if (this.velY > 0) {
            if (this.velY + velocityDelta < 0) {
                this.velY = 0;
            } else {
                this.velY += this.velocityDelta;
            }
        } else if (this.velX < 0) {
            if (this.velY + this.velocityDelta > 0) {
                this.velY = 0;
            } else {
                this.velY -= this.velocityDelta;
            }
        }

        // Update X, and Y.
        if (mobile) {
            this.x += ((this.accelerateX) + this.velX) / ((double) TPS);
            this.y += ((this.accelerateY) + this.velY) / ((double) TPS);
        }

        // Game border collision.
        Rectangle2D gameBounds = loadedGame.getGamemode().getGameBounds();
        this.prevX = x;
        this.prevY = y;
        this.x = (float) MathHelper.clamp(this.x, gameBounds.getMinX() + this.size() / 2, gameBounds.getMaxX() - size() / 2);
        this.y = (float) MathHelper.clamp(this.y, gameBounds.getMinY() + this.size() / 2, gameBounds.getMaxY() - size() / 2);

        // Leveling up.
        if (score / Constants.LEVEL_THRESHOLD > level) {
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

    }

    /**
     * Renders the player.
     * @param renderer the renderer to use.
     */
    @Override
    public void render(Renderer renderer) {
        // Don't render if the player isn't spawned.
        if (isNotSpawned()) return;

        // Fill the ship with the correct color.
        renderer.color(Color.red);
        renderer.fill(getShipArea());

        // Fill the arrow with the correct color.
        renderer.color(Color.white);
        renderer.fill(getArrowArea());
    }

    /**
     * Rotate the player.
     * @param deltaRotation amount of degrees to rotate.
     * @deprecated use {@link #rotate(float)} instead.
     */
    @Deprecated
    public void rotateDelta(int deltaRotation) {
        this.rotation += deltaRotation;
    }

    /**
     * Apply force using velocity.
     *
     * @param velocityX the amount velocity for bounce.
     * @param velocityY the amount velocity for bounce.
     * @param delta     the delta change.
     */
    public void applyForce(double velocityX, double velocityY, double delta) {
        setAcceleration(velocityX, velocityY);
    }

    private void setAcceleration(double x, double y) {
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
    public void applyForce(Point2D velocity, double delta) {
        this.applyForce(velocity.getX(), velocity.getY(), delta);
    }

    /**
     * Apply a force towards a direction.
     *
     * @param direction the direction (in degrees).
     * @param velocity the amount velocity for bounce.
     * @param delta    the delta change.
     */
    public void applyForce(float direction, float velocity, double delta) {
        double x = Math.cos(direction) * velocity;
        double y = Math.sin(direction) * velocity;
        this.applyForce(x, y, delta);
    }

    /**
     * Bounce off another entity, with given amount of velocity.
     *
     * @param source   the source entity that triggers the bounce.
     * @param velocity the amount velocity for bounce.
     * @param delta    the delta change.
     */
    public void bounceOff(Entity source, float velocity, double delta) {
        this.applyForce((float) Math.toDegrees(Math.atan2(source.getY() - y, source.getX() - x)), velocity, delta);
    }

    /**
     * Handles deleting of the player.
     * @see #delete()
     */
    @Override
    public void onDelete() {
        this.invalidate();
    }

    /**
     * Checks health, if health is zero or less, it will trigger game-over.
     * @see #getHealth()
     */
    @Override
    public void checkHealth() {
        if (health <= 0 && getEnvironment().isAlive()) {
            die();
        }
    }

    /**
     * Insta-kills the player.
     */
    public void die() {
        if (getEnvironment().isAlive()) {
            getEnvironment().triggerGameOver();
        }
    }

    /**
     * Save the player data.
     * @return the player data.
     */
    @Override
    public @NonNull CompoundTag save() {
        @NonNull CompoundTag document = super.save();

        document.putDouble("score", score);
        document.putFloat("rotation", rotation);
        return document;
    }

    /**
     * Load the player data.
     * @param tag the player data.
     */
    @Override
    public void load(CompoundTag tag) {
        super.save();

        this.score = tag.getFloat("score");
        this.rotation = tag.getFloat("rotation");
    }

    /**
     * Get the speed of the rotation.
     * The speed is in degrees per second.
     * @return the rotation speed.
     */
    public float getRotationSpeed() {
        return rotationSpeed;
    }

    /**
     * Set the speed of the rotation.
     * The speed is in degrees per second.
     * @param rotationSpeed the rotation speed to set.
     */
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    /**
     * Get the player's rotation.<br>
     * NOTE: Rotation is in degrees.
     * @return the rotation of the player.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Set the player's rotation.<br>
     * NOTE: Rotation is in degrees.
     * @param rotation the rotation to set to.
     */
    public void setRotation(float rotation) {
        this.rotation = rotation % 360;
    }

    /**
     * Rotate the player.<br>
     * NOTE: Rotation is in degrees.
     * @param rotation amount of degrees to rotate.
     */
    public void rotate(float rotation) {
        this.rotation = (this.rotation + rotation) % 360;
    }

    /**
     * Get the player's current score.
     * @return the score of the player.
     */
    public double getScore() {
        return score;
    }

    /**
     * Increment score of the player.
     * @param value amount of score to increment.
     */
    public void addScore(double value) {
        score += value;
    }

    /**
     * Decrement score of the player.
     * @param value amount of score to decrement.
     */
    public void subtractScore(double value) {
        score -= value;
    }

    /**
     * Set the player's score.
     * @param value the score of the player to set.
     */
    public void setScore(double value) {
        score = value;
    }

    /**
     * Get the player's current level.
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
     * Set current ship scale.
     *
     * @param scale the scale to set.
     * @see #getScale()
     */
    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Deactivate / activate the forwards motion.
     * @param bool true to activate, false to deactivate.
     */
    public void forward(boolean bool) {
        this.forward = bool;
    }

    /**
     * Deactivate / activate the backwards motion.
     * @param bool true to activate, false to deactivate.
     */
    public void backward(boolean bool) {
        this.backward = bool;
    }

    /**
     * Deactivate / activate the left rotation.
     * @param bool true to activate, false to deactivate.
     */
    public void left(boolean bool) {
        this.left = bool;
    }

    /**
     * Deactivate / activate the right rotation.
     * @param bool true to activate, false to deactivate.
     */
    public void right(boolean bool) {
        this.right = bool;
    }

    public double getVelocityDelta() {
        return velocityDelta;
    }

    public void setVelocityDelta(double velocityDelta) {
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
        return 40 * scale;
    }

    /**
     * Shoot a bullet from the {@linkplain #getCurrentAmmo() current ammo}.
     * @see #setCurrentAmmo(AmmoType)
     */
    public void shoot() {
        if (canShoot()) {
            shootCooldown = TimeProcessor.secondsToTicks(1.0);
            environment.spawn(new Bullet(this, currentAmmo, x, y, rotation, environment));
        }
    }

    public boolean canShoot() {
        return shootCooldown <= 0;
    }

    public int getInvincibilityTicks() {
        return invincibilityTicks;
    }
}