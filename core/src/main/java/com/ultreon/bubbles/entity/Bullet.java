package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.game.Constants;
import com.ultreon.bubbles.init.AmmoTypes;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public class Bullet extends Entity {
    private final Player owner;
    private AmmoType ammoType;
    private int popsRemaining = 4;

    public Bullet(Environment environment) {
        this(null, AmmoTypes.BASIC, 0, 0, 0, environment);
    }

    public Bullet(@UnknownNullability Player owner, @NotNull AmmoType type, float x, float y, float rotation, Environment environment) {
        super(Entities.BULLET, environment);
        this.owner = owner;

        this.x = x;
        this.y = y;
        this.setRotation(rotation);

        this.ammoType = type;
        this.attributes.setAll(type.getDefaultAttributes());

        setSpeed(type.getSpeed());

        markAsCollidable(Entities.BUBBLE);
        markAsCollidable(Entities.GIANT_BUBBLE);

        markAsAttackable(Entities.BUBBLE);
        markAsAttackable(Entities.GIANT_BUBBLE);
    }

    private void setSpeed(float speed) {
        attributes.setBase(Attribute.SPEED, speed);
    }

    @Override
    public void onCollision(Entity other, double deltaTime) {
        if (other instanceof LivingEntity livingEntity && livingEntity.isInvincible()) return;

        ammoType.onCollision(this, other, deltaTime);


        // Modifiers
        if (other instanceof Bubble bubble) {
            AttributeContainer attributeMap = bubble.getAttributes();
            double scoreMultiplier = attributeMap.getBase(Attribute.SCORE);
            double attack = attributeMap.getBase(Attribute.ATTACK);  // Maybe used.
            double defense = attributeMap.getBase(Attribute.DEFENSE);  // Maybe used.

            // Attributes
            double radius = bubble.getRadius();
            double speed = bubble.getSpeed();

            // Calculate score value.
            double visibleValue = radius * speed;
            double nonVisibleValue = attack * defense;
            double scoreValue = ((visibleValue * (nonVisibleValue + 1)) * scoreMultiplier * scoreMultiplier) * owner.getAttributes().getBase(Attribute.SCORE_MODIFIER) * deltaTime / Constants.BUBBLE_SCORE_REDUCTION;

            // Add score.
            owner.addScore(scoreValue);
        } else if (other.getAttributes().has(Attribute.SCORE_MODIFIER)) {
            double score = other.getAttributes().get(Attribute.SCORE_MODIFIER);
            owner.addScore(score);
        }
        popsRemaining--;
        if (popsRemaining <= 0) {
            delete();
        }
    }

    @Override
    public Shape getShape() {
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
