package com.ultreon.bubbles.init;

import com.ultreon.bubbles.entity.Bullet;
import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.GiantBubble;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;

import java.util.function.Supplier;

public class Entities {
    private static final DelayedRegister<EntityType<?>> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registry.ENTITIES);

    public static final RegistrySupplier<EntityType<Bullet>> BULLET = register("bullet", () -> new EntityType<>(Bullet::new));
    public static final RegistrySupplier<EntityType<Bubble>> BUBBLE = register("bubble", () -> new EntityType<>(Bubble::new));
    public static final RegistrySupplier<EntityType<GiantBubble>> GIANT_BUBBLE = register("giant_bubble", () -> new EntityType<>(GiantBubble::new));
    public static final RegistrySupplier<EntityType<Player>> PLAYER = register("player", () -> new EntityType<>(Player::new));

    @SuppressWarnings("SameParameterValue")
    private static <T extends EntityType<?>> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     */
    public static void register() {
        REGISTER.register();
    }
}
