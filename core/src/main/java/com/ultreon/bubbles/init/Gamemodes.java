package com.ultreon.bubbles.init;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.gamemode.ImpossibleMode;
import com.ultreon.bubbles.gamemode.NormalMode;
import com.ultreon.bubbles.gamemode.TimedMode;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.registries.v0.DelayedRegister;
import com.ultreon.libs.registries.v0.RegistrySupplier;
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
    public static final RegistrySupplier<NormalMode> NORMAL = Gamemodes.register("normal", NormalMode::new);
    public static final RegistrySupplier<ImpossibleMode> IMPOSSIBLE = Gamemodes.register("impossible", ImpossibleMode::new);
    public static final RegistrySupplier<TimedMode> TIMED = Gamemodes.register("timed", TimedMode::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Gamemode> RegistrySupplier<T> register(String name, Supplier<T> gamemode) {
        return REGISTER.register(name, gamemode);
    }

    @ApiStatus.Internal
    public static void register() {
        REGISTER.register();
    }
}
