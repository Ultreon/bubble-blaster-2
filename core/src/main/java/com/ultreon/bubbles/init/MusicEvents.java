package com.ultreon.bubbles.init;

import com.ultreon.bubbles.audio.MusicEvent;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;

public class MusicEvents {
    public static final MusicEvent SUBMARINE = MusicEvents.register("submarine", new MusicEvent());
    public static final MusicEvent ULTIMA = MusicEvents.register("ultima", new MusicEvent());

    @SuppressWarnings("SameParameterValue")
    private static <T extends MusicEvent> T register(String name, T sound) {
        Registries.MUSIC.register(new Identifier(name), sound);
        return sound;
    }

    public static void register() {

    }
}
