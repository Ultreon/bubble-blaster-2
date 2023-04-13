package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.object.RegistrySupplier;
import com.ultreon.bubbles.render.font.SystemFont;
import com.ultreon.bubbles.render.font.Font;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class Fonts {
    private static final DelayedRegister<Font> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registry.FONTS);

    public static final Supplier<Font> DEFAULT = () -> BubbleBlaster.getInstance().getSansFont();
    public static final RegistrySupplier<Font> MONOSPACED = register("roboto/roboto_mono", Font::new);
    public static final RegistrySupplier<Font> QUANTUM = register("quantum", Font::new);
    public static final RegistrySupplier<Font> CHICLE = register("chicle", Font::new);
    public static final RegistrySupplier<Font> PIXEL = register("pixel", Font::new);
    public static final RegistrySupplier<Font> PRESS_START_K = register("pixel/press_start_k", Font::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Font> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     */
    @ApiStatus.Internal
    public static void register() {
        REGISTER.register();
    }
}
