package com.ultreon.bubbles.init;

import com.ultreon.bubbles.gamemode.ClassicMode;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.gamemode.ImpossibleMode;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Qboi
 * @see Gamemode
 * @since 0.0.0
 */
@SuppressWarnings("unused")
public class Gamemodes {
    public static final ClassicMode CLASSIC = register("classic", new ClassicMode());
    public static final ImpossibleMode IMPOSSIBLE = register("impossible", new ImpossibleMode());

    @SuppressWarnings("SameParameterValue")
    private static <T extends Gamemode> T register(String name, T gamemode) {
        Registries.GAMEMODES.register(new Identifier(name), gamemode);
        return gamemode;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
