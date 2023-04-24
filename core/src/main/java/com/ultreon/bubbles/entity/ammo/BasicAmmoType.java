package com.ultreon.bubbles.entity.ammo;

import com.ultreon.bubbles.entity.Bullet;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class BasicAmmoType extends AmmoType {
    @Override
    public Shape getShape(Bullet entity) {
        return new Ellipse2D.Double(entity.getX() - 2.5, entity.getY() - 2.5, 5, 5);
    }

    @Override
    public float getSpeed() {
        return 384f;
    }

    @Override
    public void render(Renderer renderer, Bullet entity) {
        renderer.color(0xffdfff00);
        renderer.fill(getShape(entity));
    }

    @Override
    public AttributeContainer getDefaultAttributes() {
        AttributeContainer map = new AttributeContainer();
        map.setBase(Attribute.ATTACK, 1f);
        map.setBase(Attribute.DEFENSE, 4f);
        return map;
    }
}
