package com.ultreon.bubbles.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.Constants;
import com.ultreon.bubbles.init.AmmoTypes;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public class Bullet extends Entity {
    @Nullable
    private Player owner = null;
    private AmmoType ammoType;
    private int popsRemaining = 4;

    public Bullet(Environment environment) {
        this(AmmoTypes.BASIC, new Vector2(), 0, environment);
    }

    public Bullet(@NotNull AmmoType type, Vector2 pos, float rotation, Environment environment) {
        super(Entities.BULLET, environment);

        this.pos.set(pos);
        this.pos.set(pos);
        this.setRotation(rotation);

        this.ammoType = type;
        this.attributes.setAll(type.getDefaultAttributes());

        setSpeed(type.getSpeed());

        markAsCollidable(Entities.BUBBLE);
        markAsCollidable(Entities.GIANT_BUBBLE);

        markAsAttackable(Entities.BUBBLE);
        markAsAttackable(Entities.GIANT_BUBBLE);
    }

    public @Nullable Player getOwner() {
        return owner;
    }

    public void setOwner(@Nullable Player owner) {
        this.owner = owner;
    }

    private void setSpeed(float speed) {
        attributes.setBase(Attribute.SPEED, speed);
    }

    @Override
    public void onCollision(Entity other, double deltaTime) {
        if (this.owner == null) return;
        if (other instanceof LivingEntity livingEntity && livingEntity.isInvincible()) return;

        this.ammoType.onCollision(this, other, deltaTime);


        // Modifiers
        if (other instanceof AbstractBubbleEntity bubble) {
            // Attributes
            var attributeMap = bubble.getAttributes();
            var bubScore = attributeMap.getBase(Attribute.SCORE);
            var attack = attributeMap.getBase(Attribute.ATTACK);  // Maybe used.
            var defense = attributeMap.getBase(Attribute.DEFENSE);  // Maybe used.
            var scoreModifier = this.owner.getAttributes().getBase(Attribute.SCORE_MODIFIER);

            // Properties
            var radius = bubble.getRadius();
            var speed = bubble.getSpeed();

            // Calculate score value.
            var props = (radius * (speed + 1)) * (attack + defense + 1);
            var attrs = props * bubScore * bubScore * scoreModifier;
            var scoreValue = attrs * deltaTime / BubbleBlasterConfig.BUBBLE_SCORE_REDUCTION.get();

            // Add score.
            this.owner.awardScore(scoreValue);
        } else if (other.getAttributes().has(Attribute.SCORE_MODIFIER)) {
            var score = other.getAttributes().get(Attribute.SCORE_MODIFIER);
            this.owner.awardScore(score);
        }
        this.popsRemaining--;
        if (this.popsRemaining <= 0) {
            delete();
        }
    }

    @Override
    public Shape2D getShape() {
        return ammoType.getShape(this);
    }

    @Override
    public void render(Renderer renderer) {
        this.ammoType.render(renderer, this);
    }

    @Override
    protected void make() {

    }

    @Override
    protected void invalidate() {

    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.pos.x, this.pos.y, 1, 1);
    }

    @Override
    public double size() {
        return 3;
    }

    @Override
    protected boolean isValid() {
        return false;
    }

    @Override
    public void tick(Environment environment) {
        super.tick(environment);

        if (!isVisible()) {
            delete();
        }
    }

    @SuppressWarnings("unused")
    @Nullable
    public AmmoType getAmmoType() {
        return ammoType;
    }

    @SuppressWarnings("unused")
    public void setAmmoType(@NotNull AmmoType ammoType) {
        this.ammoType = ammoType;
        this.attributes.setAll(ammoType.getDefaultAttributes());
    }
}
