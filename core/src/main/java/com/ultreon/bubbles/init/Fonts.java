package com.ultreon.bubbles.init;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.FontUtils;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.DelayedRegister;
import com.ultreon.libs.registries.v0.RegistrySupplier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class Fonts {
    private static final DelayedRegister<BitmapFont> REGISTER = DelayedRegister.create(Identifier.getDefaultNamespace(), Registries.BITMAP_FONTS);

    public static final Supplier<BitmapFont> DEFAULT = () -> BubbleBlaster.getInstance().getSansFont();

    public static final RegistrySupplier<BitmapFont> MONOSPACED_12 = Fonts.register("monospaced_12", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_regular"), 12));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_14 = Fonts.register("monospaced_14", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_regular"), 14));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_15 = Fonts.register("monospaced_15", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_regular"), 15));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_20 = Fonts.register("monospaced_20", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_regular"), 20));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_24 = Fonts.register("monospaced_24", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_regular"), 24));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_BOLD_12 = Fonts.register("monospaced_bold_12", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 12));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_BOLD_14 = Fonts.register("monospaced_bold_14", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 14));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_BOLD_15 = Fonts.register("monospaced_bold_15", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 15));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_BOLD_20 = Fonts.register("monospaced_bold_20", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 20));
    public static final RegistrySupplier<BitmapFont> MONOSPACED_BOLD_24 = Fonts.register("monospaced_bold_24", () -> FontUtils.createBitmapFont(BubbleBlaster.id("roboto/roboto_mono_bold"), 24));
    public static final RegistrySupplier<BitmapFont> DONGLE_60 = Fonts.register("dongle_pause", () -> FontUtils.createBitmapFont(BubbleBlaster.id("dongle/dongle_bold"), 60));
    public static final RegistrySupplier<BitmapFont> DONGLE_75 = Fonts.register("dongle_pause", () -> FontUtils.createBitmapFont(BubbleBlaster.id("dongle/dongle_bold"), 75));
    public static final RegistrySupplier<BitmapFont> DONGLE_140 = Fonts.register("dongle_title", () -> FontUtils.createBitmapFont(BubbleBlaster.id("dongle/dongle_bold"), 140));
    public static final RegistrySupplier<BitmapFont> CHICLE_14 = Fonts.register("chicle", () -> FontUtils.createBitmapFont(BubbleBlaster.id("chicle"), 14));
    public static final RegistrySupplier<BitmapFont> PIXEL_14 = Fonts.register("pixel", () -> FontUtils.createBitmapFont(BubbleBlaster.id("pixel"), 14));
    public static final RegistrySupplier<BitmapFont> PRESS_START_K_14 = Fonts.register("press_start_k", () -> FontUtils.createBitmapFont(BubbleBlaster.id("pixel/press_start_k"), 14));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_12 = Fonts.register("sans_regular_12", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 12));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_14 = Fonts.register("sans_regular_14", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 14));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_15 = Fonts.register("sans_regular_15", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 15));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_16 = Fonts.register("sans_regular_16", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 16));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_20 = Fonts.register("sans_regular_20", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 20));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_32 = Fonts.register("sans_regular_32", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 32));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_36 = Fonts.register("sans_regular_36", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 36));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_40 = Fonts.register("sans_regular_40", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 40));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_48 = Fonts.register("sans_regular_48", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 48));
    public static final RegistrySupplier<BitmapFont> SANS_REGULAR_60 = Fonts.register("sans_regular_60", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 60));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_12 = Fonts.register("sans_bold_12", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 12));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_14 = Fonts.register("sans_bold_14", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 14));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_15 = Fonts.register("sans_bold_15", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 15));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_20 = Fonts.register("sans_bold_20", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 20));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_24 = Fonts.register("sans_bold_24", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 24));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_32 = Fonts.register("sans_bold_32", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 32));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_36 = Fonts.register("sans_bold_36", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 36));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_40 = Fonts.register("sans_bold_40", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 40));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_48 = Fonts.register("sans_bold_48", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 48));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_60 = Fonts.register("sans_bold_60", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold"), 60));
    public static final RegistrySupplier<BitmapFont> SANS_ITALIC_16 = Fonts.register("sans_italic_16", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_italic"), 16));
    public static final RegistrySupplier<BitmapFont> SANS_ITALIC_20 = Fonts.register("sans_italic_20", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_italic"), 20));
    public static final RegistrySupplier<BitmapFont> SANS_BOLD_ITALIC_10 = Fonts.register("sans_bold_italic_10", () -> FontUtils.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_bold_italic"), 10));

    @SuppressWarnings("SameParameterValue")
    private static <T extends BitmapFont> RegistrySupplier<T> register(String name, Supplier<T> font) {

        return REGISTER.register(name, font);
    }

    @ApiStatus.Internal
    public static void register() {
        REGISTER.register();
    }
}
