package com.ultreon.bubbles.init;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.gui.hud.ClassicHud;
import com.ultreon.bubbles.render.gui.hud.HudType;
import com.ultreon.bubbles.render.gui.hud.LegacyHud;
import com.ultreon.bubbles.render.gui.hud.ModernHud;
import com.ultreon.libs.registries.v0.DelayedRegister;
import com.ultreon.libs.registries.v0.RegistrySupplier;

import java.util.function.Supplier;

public class HudTypes {
    private static final DelayedRegister<HudType> REGISTER = DelayedRegister.create(BubbleBlaster.NAMESPACE, Registries.HUD);
    public static final RegistrySupplier<LegacyHud> LEGACY = HudTypes.register("legacy", LegacyHud::new);
    public static final RegistrySupplier<ClassicHud> CLASSIC = HudTypes.register("beta", ClassicHud::new);
    public static final RegistrySupplier<ModernHud> MODERN = HudTypes.register("modern", ModernHud::new);

    private static <T extends HudType> RegistrySupplier<T> register(String name, Supplier<T> sound) {
        return REGISTER.register(name, sound);
    }

    public static void register() {
        REGISTER.register();
    }
}
