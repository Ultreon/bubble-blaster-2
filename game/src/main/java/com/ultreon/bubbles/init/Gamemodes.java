package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.gamemode.ClassicMode;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;

import java.util.function.Supplier;

/**
 * @author Qboi
 * @see Gamemode
 * @since 0.0.0
 */
@SuppressWarnings("unused")
//@ObjectHolder(modId = "bubbleblaster", type = GameType.class)
public class Gamemodes {
    private static final DelayedRegister<Gamemode> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registry.GAMEMODES);

    public static final RegistrySupplier<ClassicMode> CLASSIC = register("Classic", ClassicMode::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Gamemode> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     */
    public static void register() {
        REGISTER.register();
    }
}
