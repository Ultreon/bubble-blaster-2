package com.ultreon.bubbles.entity.ammo;

import com.google.common.annotations.Beta;
import com.ultreon.bubbles.entity.Bullet;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.common.Registrable;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.item.ItemType;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

/**
 * Ammo type, handles things for {@link Bullet}.
 * This also adds a new {@link ItemType} to the registry.
 *
 * @author Qboi123
 */
@Beta
public abstract class AmmoType extends Registrable {
    public abstract void render(Renderer renderer, Bullet rotation);

    /**
     * Handles collision for the given bullet if the bullet has {@link AmmoType this} as ammo type.
     *
     * @param bullet    bullet entity.
     * @param collided  the other entity that the bullet collided with.
     * @param deltaTime the collision delta time.
     */
    public void onCollision(Bullet bullet, Entity collided, double deltaTime) {

    }

    public abstract AttributeContainer getDefaultAttributes();

    public abstract Shape getShape(Bullet bullet);

    @Override
    public String toString() {
        return "AmmoType[" + id() + "]";
    }

    public abstract float getSpeed();
}
