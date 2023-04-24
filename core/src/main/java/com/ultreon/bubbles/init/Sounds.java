package com.ultreon.bubbles.init;

import com.ultreon.bubbles.media.Sound;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;

public class Sounds {
    public static final Sound MENU_EVENT = register("sfx/ui/button/focus_change", new Sound());

    @SuppressWarnings("SameParameterValue")
    private static <T extends Sound> T register(String name, T sound) {
        Registries.SOUNDS.register(new Identifier(name), sound);
        return sound;
    }

    public static void register() {

    }
}
