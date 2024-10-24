package dev.ultreon.bubbles.entity.ammo;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;
import dev.ultreon.bubbles.entity.Bullet;
import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.entity.attribute.AttributeContainer;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;

public class BasicAmmoType extends AmmoType {
    @Override
    public Shape2D getShape(Bullet entity) {
        return new Circle(entity.getX() - 2.5f, entity.getY() - 2.5f, 5);
    }

    @Override
    public float getSpeed() {
        return 384f;
    }

    @Override
    public void render(Renderer renderer, Bullet entity) {
        renderer.fill(this.getShape(entity), Color.rgb(0xdfff00));
    }

    @Override
    public AttributeContainer getDefaultAttributes() {
        var map = new AttributeContainer();
        map.setBase(Attribute.ATTACK, 1f);
        map.setBase(Attribute.DEFENSE, 4f);
        return map;
    }
}
