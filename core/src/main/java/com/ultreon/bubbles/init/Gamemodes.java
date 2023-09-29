package com.ultreon.bubbles.init;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.gamemode.*;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.DelayedRegister;
import com.ultreon.libs.registries.v0.RegistrySupplier;
import org.apache.regexp.RE;
import org.checkerframework.nonapi.io.github.classgraph.json.Id;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * @author XyperCode
 * @see Gamemode
 * @since 0.0.0
 */
@SuppressWarnings("unused")
public class Gamemodes {
    private static final DelayedRegister<Gamemode> REGISTER = DelayedRegister.create(BubbleBlaster.NAMESPACE, Registries.GAMEMODES);

    public static final RegistrySupplier<LegacyMode> LEGACY = register("legacy", LegacyMode::new);
    public static final RegistrySupplier<ClassicMode> CLASSIC = register("classic", ClassicMode::new);
    public static final RegistrySupplier<ModernMode> MODERN = register("modern", ModernMode::new);
    public static final RegistrySupplier<ImpossibleMode> IMPOSSIBLE = register("impossible", ImpossibleMode::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Gamemode> RegistrySupplier<T> register(String name, Supplier<T> gamemode) {
        return REGISTER.register(name, gamemode);
    }

    @ApiStatus.Internal
    public static void register() {
        REGISTER.register();
    }
}
