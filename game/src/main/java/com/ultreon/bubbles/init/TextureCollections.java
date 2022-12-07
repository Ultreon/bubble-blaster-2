package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registers;
import com.ultreon.bubbles.registry.object.RegistrySupplier;
import com.ultreon.bubbles.render.TextureCollection;

import java.util.function.Supplier;

/**
 * Initialization for texture collections.
 *
 * @see Registers#TEXTURE_COLLECTIONS
 * @since 1.0.924-a1
 */
public class TextureCollections {
    private static final DelayedRegister<TextureCollection> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registers.TEXTURE_COLLECTIONS);

    public static final RegistrySupplier<TextureCollection> BUBBLE_TEXTURES = register("Bubble", TextureCollection::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends TextureCollection> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     */
    public static void register() {
        REGISTER.register();
    }
}
