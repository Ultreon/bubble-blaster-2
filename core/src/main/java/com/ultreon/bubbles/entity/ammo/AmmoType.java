package com.ultreon.bubbles.entity.ammo;

import com.badlogic.gdx.math.Shape2D;
import com.google.common.annotations.Beta;
import com.ultreon.bubbles.entity.Bullet;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.item.ItemType;
import com.ultreon.bubbles.render.Renderer;


/**
 * Ammo type, handles things for {@link Bullet}.
 * This also adds a new {@link ItemType} to the registry.
 *
 * @author XyperCode
 */
@Beta
public abstract class AmmoType {
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

    public abstract Shape2D getShape(Bullet bullet);

    public abstract float getSpeed();
}
