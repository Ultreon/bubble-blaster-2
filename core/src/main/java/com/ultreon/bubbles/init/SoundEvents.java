package com.ultreon.bubbles.init;

import com.ultreon.bubbles.media.SoundEvent;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;

public class SoundEvents {
    public static final SoundEvent MENU_EVENT = register("sfx/ui/button/focus_change", new SoundEvent());

    @SuppressWarnings("SameParameterValue")
    private static <T extends SoundEvent> T register(String name, T sound) {
        Registries.SOUNDS.register(new Identifier(name), sound);
        return sound;
    }

    public static void register() {

    }
}
