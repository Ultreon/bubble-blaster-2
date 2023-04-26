package com.ultreon.bubbles.init;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.font.Font;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class Fonts {
    public static final Supplier<Font> DEFAULT = () -> BubbleBlaster.getInstance().getSansFont();
    public static final Font MONOSPACED = register("roboto/roboto", new Font());
    public static final Font QUANTUM = register("quantum", new Font());
    public static final Font DONGLE = register("dongle/dongle", new Font());
    public static final Font CHICLE = register("chicle", new Font());
    public static final Font PIXEL = register("pixel", new Font());
    public static final Font PRESS_START_K = register("pixel/press_start_k", new Font());

    @SuppressWarnings("SameParameterValue")
    private static <T extends Font> T register(String name, T font) {
        Registries.FONTS.register(new Identifier(name), font);
        return font;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
