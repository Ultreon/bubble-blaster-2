package com.ultreon.bubbles.init;

import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Bullet;
import com.ultreon.bubbles.entity.GiantBubble;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

public class Entities {
    public static final EntityType<Bullet> BULLET = register("bullet", new EntityType<>(Bullet::new));
    public static final EntityType<Bubble> BUBBLE = register("bubble", new EntityType<>(Bubble::new));
    public static final EntityType<GiantBubble> GIANT_BUBBLE = register("giant_bubble", new EntityType<>(GiantBubble::new));
    public static final EntityType<Player> PLAYER = register("player", new EntityType<>(Player::new));

    @SuppressWarnings("SameParameterValue")
    private static <T extends EntityType<?>> T register(String name, T entityType) {
        Registries.ENTITIES.register(new Identifier(name), entityType);
        return entityType;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
