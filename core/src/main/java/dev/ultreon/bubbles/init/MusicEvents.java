package dev.ultreon.bubbles.init;

import dev.ultreon.bubbles.audio.MusicEvent;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.libs.commons.v0.Identifier;

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
