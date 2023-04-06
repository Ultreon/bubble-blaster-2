package com.ultreon.bubbles.init;

import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.media.Sound;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;

import java.util.function.Supplier;

public class Sounds {
    private static final DelayedRegister<Sound> REGISTER = DelayedRegister.create(BubbleBlaster.NAMESPACE, Registry.SOUNDS);

    public static final RegistrySupplier<Sound> MENU_EVENT = register("sfx/ui/button/focus_change", Sound::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Sound> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    public static void register() {
        REGISTER.register();
    }
}
