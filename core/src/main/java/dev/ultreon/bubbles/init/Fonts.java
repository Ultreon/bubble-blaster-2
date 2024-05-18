package dev.ultreon.bubbles.init;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.registries.v0.DelayedRegister;
import dev.ultreon.libs.registries.v0.RegistrySupplier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class Fonts {
    private static final DelayedRegister<BitmapFont> REGISTER = DelayedRegister.create(Identifier.getDefaultNamespace(), Registries.BITMAP_FONTS);

    public static final Supplier<BitmapFont> DEFAULT = () -> BubbleBlaster.getInstance().getSansFont();

    public static final RegistrySupplier<BitmapFont> MONOSPACED = Fonts.register("monospaced", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_regular"), 16));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_BOLD = Fonts.register("monospaced_bold", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 16));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_HEADING_3 = Fonts.register("monospaced_heading_2", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 20));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_HEADING_2 = Fonts.register("monospaced_heading_2", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 24));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_HEADING_1 = Fonts.register("monospaced_heading_1", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 32));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_TITLE = Fonts.register("monospaced_heading_1", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 40));
    public static final RegistrySupplier<BitmapFont> DONGLE_GAME_OVER = Fonts.register("dongle_game_over", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("dongle/dongle_bold"), 60));
    public static final RegistrySupplier<BitmapFont> DONGLE_PAUSE = Fonts.register("dongle_pause", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("dongle/dongle_bold"), 75));
    public static final RegistrySupplier<BitmapFont> DONGLE_TITLE = Fonts.register("dongle_title", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("dongle/dongle_bold"), 140));
    public static final RegistrySupplier<BitmapFont> GAME_GLITCH = Fonts.register("game_glitch", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("pixel/press_start_k"), 14));
    public static final RegistrySupplier<BitmapFont> SANS_PARAGRAPH = Fonts.register("sans_paragraph", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 16));
    public static final RegistrySupplier<BitmapFont> SANS_BIG = Fonts.register("sans_big", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 20));
    public static final RegistrySupplier<BitmapFont> SANS_PARAGRAPH_BOLD = Fonts.register("sans_paragraph_bold", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 16));
    public static final RegistrySupplier<BitmapFont> SANS_HEADER_3 = Fonts.register("sans_header_3", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 20));
    public static final RegistrySupplier<BitmapFont> SANS_HEADER_2 = Fonts.register("sans_header_2", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 24));
    public static final RegistrySupplier<BitmapFont> SANS_HEADER_1 = Fonts.register("sans_header_1", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 32));
    public static final RegistrySupplier<BitmapFont> SANS_TITLE = Fonts.register("sans_title", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 40));
    public static final RegistrySupplier<BitmapFont> SANS_GIANT = Fonts.register("sans_giant", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 60));
    public static final RegistrySupplier<BitmapFont> SANS_ITALIC = Fonts.register("sans_italic", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_italic"), 16));
    public static final RegistrySupplier<BitmapFont> SANS_BETA_LEVEL_UP = Fonts.register("sans_beta_font", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 40));
    public static final RegistrySupplier<BitmapFont> SANS_BETA_INFO = Fonts.register("sans_beta_info", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 16));
    public static final RegistrySupplier<BitmapFont> SANS_BETA_FPS = Fonts.register("sans_beta_fps", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_italic"), 20));
    public static final RegistrySupplier<BitmapFont> SANS_SUBTITLE = Fonts.register("sans_subtitle", () -> BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold_italic"), 10));

    @SuppressWarnings("SameParameterValue")
    private static <T extends BitmapFont> RegistrySupplier<T> register(String name, Supplier<T> font) {

        return REGISTER.register(name, font);
    }

    @ApiStatus.Internal
    public static void register() {
        REGISTER.register();
    }
}
