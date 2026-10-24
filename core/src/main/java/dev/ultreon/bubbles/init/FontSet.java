package dev.ultreon.bubbles.init;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.translations.v1.LanguageManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** A font style and size which resolves to a language-specific font on demand. */
public final class FontSet extends BitmapFont {
    private final String family;
    private final String style;
    private final int size;
    private final Map<String, BitmapFont> fonts = new HashMap<>();

    public FontSet(String family, String style, int size) {
        super(new BitmapFont.BitmapFontData(), new TextureRegion(), false);
        this.family = family;
        this.style = style;
        this.size = size;
    }

    public static FontSet fromIdentifier(Identifier id, int size) {
        var path = id.path();
        var slash = path.lastIndexOf('/');
        var family = slash < 0 ? path : path.substring(0, slash);
        var fileName = slash < 0 ? path : path.substring(slash + 1);
        if (family.equals("noto_sans") && fileName.startsWith("noto_sans_")) {
            return new FontSet(family, fileName.substring("noto_sans_".length()), size);
        }
        return new FontSet(path, "", size);
    }

    public BitmapFont get() {
        var locale = LanguageManager.getCurrentLanguage();
        var language = locale == null ? "en" : locale.getLanguage();
        var script = switch (language) {
            case "ja" -> "jp";
            case "zh" -> switch (locale.getCountry()) {
                case "TW", "HK", "MO" -> "tc";
                default -> "sc";
            };
            case "ko" -> "ko";
            case "th" -> "thai";
            case "he", "iw" -> "hebrew";
            default -> "";
        };
        var key = script + ":" + language;
        return fonts.computeIfAbsent(key, ignored -> {
            if (!family.equals("noto_sans")) {
                return BubbleBlaster.createBitmapFont(BubbleBlaster.id(family), size);
            }
            var base = script.isEmpty() ? "noto_sans/noto_sans_" : "noto_sans/noto_sans_" + script + "_";
            var id = BubbleBlaster.id(base + style);
            try {
                return BubbleBlaster.createBitmapFont(id, size);
            } catch (RuntimeException ex) {
                return BubbleBlaster.createBitmapFont(BubbleBlaster.id("noto_sans/noto_sans_" + style), size);
            }
        });
    }

    @Override public float getLineHeight() { return get().getLineHeight(); }
    @Override public float getDescent() { return get().getDescent(); }
    @Override public float getXHeight() { return get().getXHeight(); }
    @Override public float getCapHeight() { return get().getCapHeight(); }
}
