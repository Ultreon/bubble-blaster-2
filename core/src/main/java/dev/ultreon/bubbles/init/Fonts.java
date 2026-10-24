package dev.ultreon.bubbles.init;

import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.registries.v0.DelayedRegister;
import dev.ultreon.libs.registries.v0.RegistrySupplier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;
import dev.ultreon.bubbles.BubbleBlaster;

public final class Fonts {
    private static final DelayedRegister<FontSet> REGISTER = DelayedRegister.create(Identifier.getDefaultNamespace(), Registries.BITMAP_FONTS);

    public static final Supplier<FontSet> DEFAULT = () -> BubbleBlaster.getInstance().getSansFont();

    public static final RegistrySupplier<FontSet> MONOSPACED = register("monospaced", "roboto/roboto_mono_regular", 16);
    public static final RegistrySupplier<FontSet> MONOSPACED_BOLD = register("monospaced_bold", "roboto/roboto_mono_bold", 16);
    public static final RegistrySupplier<FontSet> MONOSPACED_HEADING_3 = register("monospaced_heading_3", "roboto/roboto_mono_bold", 20);
    public static final RegistrySupplier<FontSet> MONOSPACED_HEADING_2 = register("monospaced_heading_2", "roboto/roboto_mono_bold", 24);
    public static final RegistrySupplier<FontSet> MONOSPACED_HEADING_1 = register("monospaced_heading_1", "roboto/roboto_mono_bold", 32);
    public static final RegistrySupplier<FontSet> MONOSPACED_TITLE = register("monospaced_title", "roboto/roboto_mono_bold", 40);
    public static final RegistrySupplier<FontSet> DONGLE_GAME_OVER = register("dongle_game_over", "dongle/dongle_bold", 60);
    public static final RegistrySupplier<FontSet> DONGLE_PAUSE = register("dongle_pause", "dongle/dongle_bold", 75);
    public static final RegistrySupplier<FontSet> DONGLE_TITLE = register("dongle_title", "dongle/dongle_bold", 140);
    public static final RegistrySupplier<FontSet> GAME_GLITCH = register("game_glitch", "pixel/press_start_2p_bold", 14);
    public static final RegistrySupplier<FontSet> SANS_PARAGRAPH = register("sans_paragraph", "noto_sans/noto_sans_bold", 16);
    public static final RegistrySupplier<FontSet> SANS_BIG = register("sans_big", "noto_sans/noto_sans_bold", 20);
    public static final RegistrySupplier<FontSet> SANS_PARAGRAPH_BOLD = register("sans_paragraph_bold", "noto_sans/noto_sans_black", 16);
    public static final RegistrySupplier<FontSet> SANS_HEADER_3 = register("sans_header_3", "noto_sans/noto_sans_black", 20);
    public static final RegistrySupplier<FontSet> SANS_HEADER_2 = register("sans_header_2", "noto_sans/noto_sans_black", 24);
    public static final RegistrySupplier<FontSet> SANS_HEADER_1 = register("sans_header_1", "noto_sans/noto_sans_black", 32);
    public static final RegistrySupplier<FontSet> SANS_TITLE = register("sans_title", "noto_sans/noto_sans_black", 40);
    public static final RegistrySupplier<FontSet> SANS_GIANT = register("sans_giant", "noto_sans/noto_sans_black", 60);
    public static final RegistrySupplier<FontSet> SANS_ITALIC = register("sans_italic", "noto_sans/noto_sans_regular", 16);
    public static final RegistrySupplier<FontSet> SANS_BETA_LEVEL_UP = register("sans_beta_font", "noto_sans/noto_sans_bold", 40);
    public static final RegistrySupplier<FontSet> SANS_BETA_INFO = register("sans_beta_info", "noto_sans/noto_sans_black", 16);
    public static final RegistrySupplier<FontSet> SANS_BETA_FPS = register("sans_beta_fps", "noto_sans/noto_sans_regular", 20);
    public static final RegistrySupplier<FontSet> SANS_SUBTITLE = register("sans_subtitle", "noto_sans/noto_sans_black", 10);

    private static RegistrySupplier<FontSet> register(String name, String path, int size) {
        var slash = path.lastIndexOf('/');
        var family = path.substring(0, slash);
        var fileName = path.substring(slash + 1);
        var style = fileName.substring("noto_sans_".length());
        if (!family.equals("noto_sans")) return REGISTER.register(name, () -> new FontSet(path, "", size));
        return REGISTER.register(name, () -> new FontSet(family, style, size));
    }

    @ApiStatus.Internal
    public static void register() { REGISTER.register(); }
}
