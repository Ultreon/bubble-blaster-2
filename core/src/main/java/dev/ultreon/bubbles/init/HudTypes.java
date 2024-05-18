package dev.ultreon.bubbles.init;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.render.gui.hud.*;
import dev.ultreon.libs.registries.v0.DelayedRegister;
import dev.ultreon.libs.registries.v0.RegistrySupplier;

import java.util.function.Supplier;

public class HudTypes {
    private static final DelayedRegister<HudType> REGISTER = DelayedRegister.create(BubbleBlaster.NAMESPACE, Registries.HUD);

    public static final RegistrySupplier<LegacyHud> LEGACY = HudTypes.register("legacy", LegacyHud::new);
    public static final RegistrySupplier<BetaHud> BETA = HudTypes.register("beta", BetaHud::new);
    public static final RegistrySupplier<ModernHud> MODERN = HudTypes.register("modern", ModernHud::new);
    public static final RegistrySupplier<TimedHud> TIMED = HudTypes.register("timed", TimedHud::new);

    private static <T extends HudType> RegistrySupplier<T> register(String name, Supplier<T> sound) {
        return REGISTER.register(name, sound);
    }

    public static void register() {
        REGISTER.register();
    }
}
