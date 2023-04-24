package com.ultreon.bubbles.init;

import com.ultreon.bubbles.entity.ammo.AmmoType;
import com.ultreon.bubbles.entity.ammo.BasicAmmoType;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * @see AmmoType
 */
public class AmmoTypes {
    public static final BasicAmmoType BASIC = register("basic", new BasicAmmoType());

    @SuppressWarnings("SameParameterValue")
    private static <T extends AmmoType> T register(String name, T ammoType) {
        Registries.AMMO_TYPES.register(new Identifier(name), ammoType);
        return ammoType;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
