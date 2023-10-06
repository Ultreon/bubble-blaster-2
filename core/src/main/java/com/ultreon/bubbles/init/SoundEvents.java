package com.ultreon.bubbles.init;

import com.ultreon.bubbles.audio.SoundEvent;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;

public class SoundEvents {
    public static final SoundEvent MENU_EVENT = SoundEvents.register("ui/button/focus_change", new SoundEvent());
    public static final SoundEvent BUBBLE_POP = SoundEvents.register("bubble/pop", new SoundEvent(true));

    @SuppressWarnings("SameParameterValue")
    private static <T extends SoundEvent> T register(String name, T sound) {
        Registries.SOUNDS.register(new Identifier(name), sound);
        return sound;
    }

    public static void register() {

    }
}
