package com.ultreon.bubbles.init;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Bullet;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

public class Entities {
    public static final EntityType<Entity> UNDEFINED_TYPE = Entities.register("none", new EntityType<>(Undefined::new));
    public static final EntityType<Bullet> BULLET = Entities.register("bullet", new EntityType<>(Bullet::new));
    public static final EntityType<Bubble> BUBBLE = Entities.register("bubble", new EntityType<>(Bubble::new));
    public static final EntityType<Player> PLAYER = Entities.register("player", new EntityType<>(Player::new));

    @SuppressWarnings("SameParameterValue")
    private static <T extends EntityType<?>> T register(String name, T entityType) {
        Registries.ENTITIES.register(new Identifier(name), entityType);
        return entityType;
    }

    @ApiStatus.Internal
    public static void register() {

    }

    private static class Undefined extends Entity {
        public Undefined(World world) {
            super(UNDEFINED_TYPE, world);
        }

        @Override
        public Shape2D getShape() {
            return new Rectangle();
        }

        @Override
        public Rectangle getBounds() {
            return new Rectangle();
        }

        @Override
        public float radius() {
            return 0;
        }

        @Override
        public void render(Renderer renderer) {

        }
    }
}
