package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.InternalMod;
import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.ammo.BasicAmmoType;
import com.ultreon.bubbles.registry.DelayedRegister;
import com.ultreon.bubbles.registry.Registers;
import com.ultreon.bubbles.registry.object.RegistrySupplier;

import java.util.function.Supplier;

/**
 * @see AmmoType
 */
public class AmmoTypes {
    private static final DelayedRegister<AmmoType> REGISTER = DelayedRegister.create(InternalMod.MOD_ID, Registers.AMMO_TYPES);

    public static final RegistrySupplier<BasicAmmoType> BASIC = register("Basic", BasicAmmoType::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends AmmoType> RegistrySupplier<T> register(String name, Supplier<T> supplier) {
        return REGISTER.register(name, supplier);
    }

    /**
     * <b>DO NOT CALL, THIS IS CALLED INTERNALLY</b>
     */
    public static void register() {
        REGISTER.register();
    }
}
