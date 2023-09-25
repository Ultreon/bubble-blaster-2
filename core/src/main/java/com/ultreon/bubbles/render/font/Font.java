package com.ultreon.bubbles.render.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.commons.util.StringUtils;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.text.v0.TextObject;
import org.jetbrains.annotations.ApiStatus;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Font {
    protected final BubbleBlaster game = BubbleBlaster.getInstance();
    protected final Map<String, FontInfo> alternatives = new HashMap<>();

    FontInfo info;
    private BitmapFont bitmapFont;

    public Font() {
        GameEvents.LOAD_FONTS.listen(loader -> register());
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y) {
        drawMultiline(renderer, text.getText(), size, x, y, Anchor.NW);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, Anchor anchor) {
        drawMultiline(renderer, text.getText(), size, x, y, Thickness.REGULAR, anchor);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, int thickness) {
        drawMultiline(renderer, text, size, x, y, thickness, Anchor.NW);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, int thickness, Anchor anchor) {
        drawMultiline(renderer, text.getText(), size, x, y, thickness, FontStyle.PLAIN, anchor);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, Thickness thickness) {
        drawMultiline(renderer, text, size, x, y, thickness, Anchor.NW);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, Thickness thickness, Anchor anchor) {
        drawMultiline(renderer, text.getText(), size, x, y, thickness, FontStyle.PLAIN, anchor);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, int thickness, FontStyle style, Anchor anchor) {
        drawMultiline(renderer, text.getText(), size, x, y, Thickness.closestTo(thickness), style, anchor);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, FontStyle style) {
        drawMultiline(renderer, text, size, x, y, style, Anchor.NW);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, FontStyle style, Anchor anchor) {
        drawMultiline(renderer, text, size, x, y, Thickness.REGULAR, style, anchor);
    }

    public void drawMultiline(Renderer renderer, TextObject text, int size, float x, float y, Thickness thickness, FontStyle style, Anchor anchor) {
        drawMultiline(renderer, text.getText(), size, x, y, thickness, style, anchor);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y) {
        drawMultiline(renderer, text, size, x, y, Anchor.NW);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, Anchor anchor) {
        drawMultiline(renderer, text, size, x, y, Thickness.REGULAR, anchor);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, int thickness) {
        drawMultiline(renderer, text, size, x, y, thickness, Anchor.NW);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, int thickness, Anchor anchor) {
        drawMultiline(renderer, text, size, x, y, thickness, FontStyle.PLAIN, anchor);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, Thickness thickness) {
        drawMultiline(renderer, text, size, x, y, thickness, Anchor.NW);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, Thickness thickness, Anchor anchor) {
        drawMultiline(renderer, text, size, x, y, thickness, FontStyle.PLAIN, anchor);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, int thickness, FontStyle style, Anchor anchor) {
        drawMultiline(renderer, text, size, x, y, Thickness.closestTo(thickness), style, anchor);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, FontStyle style) {
        drawMultiline(renderer, text, size, x, y, style, Anchor.NW);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, FontStyle style, Anchor anchor) {
        drawMultiline(renderer, text, size, x, y, Thickness.REGULAR, style, anchor);
    }

    public void drawMultiline(Renderer renderer, String text, int size, float x, float y, Thickness thickness, FontStyle style, Anchor anchor) {
        AtomicInteger i = new AtomicInteger();
        text.lines().forEachOrdered(line -> draw(renderer, line, size, x, y + (height(size) + 1) * i.getAndIncrement(), thickness, style, anchor));
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y) {
        draw(renderer, text, size, x, y, Anchor.NW);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, Anchor anchor) {
        draw(renderer, text, size, x, y, Thickness.REGULAR, anchor);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, int thickness) {
        draw(renderer, text, size, x, y, thickness, Anchor.NW);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, int thickness, Anchor anchor) {
        draw(renderer, text, size, x, y, thickness, FontStyle.PLAIN, anchor);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, Thickness thickness) {
        draw(renderer, text, size, x, y, thickness, Anchor.NW);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, Thickness thickness, Anchor anchor) {
        draw(renderer, text, size, x, y, thickness, FontStyle.PLAIN, anchor);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, int thickness, FontStyle style, Anchor anchor) {
        draw(renderer, text, size, x, y, Thickness.closestTo(thickness), style, anchor);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, FontStyle style) {
        draw(renderer, text, size, x, y, style, Anchor.NW);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, FontStyle style, Anchor anchor) {
        draw(renderer, text, size, x, y, Thickness.REGULAR, style, anchor);
    }

    public void draw(Renderer renderer, TextObject text, int size, float x, float y, Thickness thickness, FontStyle style, Anchor anchor) {
        BitmapFont awtFont = getGdxFont(size, thickness, style);
        String string = text.getText();
        if (text.getText().isEmpty()) return;

        BitmapFont fallback = getGdxFont(size, thickness, style, GameSettings.instance().getLanguage());
        BitmapFont font = getGdxFont(size, thickness, style);

        var bounds = bounds(size, thickness, style, text);

        switch (anchor) {
            case N -> {
                x = x - bounds.width / 2;
                y = y + bounds.height;
            }
            case NE -> {
                x = x - bounds.width;
                y = y + bounds.height;
            }
            case NW -> y = y + bounds.height;
            case E -> {
                x = x - bounds.width;
                y = y + bounds.height / 2;
            }
            case CENTER -> {
                x = x - bounds.width / 2;
                y = y + bounds.height / 2;
            }
            case W -> y = y;
            case SE -> {
                x = x - bounds.width;
                y = y;
            }
            case S -> {
                x = x - bounds.width / 2;
                y = y;
            }
            case SW -> y = y;
        }
        renderer.setFont(font);
        renderer.drawText(string, x, y);
    }

    private Rectangle2D.Float bounds(TextObject text) {
        String s = text.getText();
        GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, s);
        return new Rectangle2D.Float(0, 0, glyphLayout.width, glyphLayout.height);
    }

    private Rectangle2D.Float bounds(String text) {
        GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text);
        return new Rectangle2D.Float(0, 0, glyphLayout.width, glyphLayout.height);
    }

    public Rectangle2D.Float bounds(int size, FontStyle style, TextObject text) {
        return bounds(size, Thickness.REGULAR, style, text);
    }

    public Rectangle2D.Float bounds(int size, TextObject text) {
        return bounds(size, Thickness.REGULAR, text);
    }

    public Rectangle2D.Float bounds(int size, Thickness thickness, TextObject text) {
        return bounds(size, thickness, FontStyle.PLAIN, text);
    }

    public Rectangle2D.Float bounds(int size, Thickness thickness, FontStyle style, TextObject text) {
        String string = text.getText();
        BitmapFont bitmapFont = getGdxFont(size, thickness, style);
        GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, string);
        return new Rectangle2D.Float(0, 0, glyphLayout.width, glyphLayout.height);
    }

    public Rectangle2D.Float bounds(int size, Thickness thickness, FontStyle style, String text) {
        BitmapFont bitmapFont = getGdxFont(size, thickness, style);
        GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text);
        return new Rectangle2D.Float(0, 0, glyphLayout.width, glyphLayout.height);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y) {
        draw(renderer, text, size, x, y, Anchor.NW);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, Anchor anchor) {
        draw(renderer, text, size, x, y, Thickness.REGULAR, anchor);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, int thickness) {
        draw(renderer, text, size, x, y, thickness, Anchor.NW);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, int thickness, Anchor anchor) {
        draw(renderer, text, size, x, y, thickness, FontStyle.PLAIN, anchor);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, Thickness thickness) {
        draw(renderer, text, size, x, y, thickness, Anchor.NW);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, Thickness thickness, Anchor anchor) {
        draw(renderer, text, size, x, y, thickness, FontStyle.PLAIN, anchor);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, int thickness, FontStyle style, Anchor anchor) {
        draw(renderer, text, size, x, y, Thickness.closestTo(thickness), style, anchor);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, FontStyle style) {
        draw(renderer, text, size, x, y, style, Anchor.NW);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, FontStyle style, Anchor anchor) {
        draw(renderer, text, size, x, y, Thickness.REGULAR, style, anchor);
    }

    public void draw(Renderer renderer, String text, int size, float x, float y, Thickness thickness, FontStyle style, Anchor anchor) {
        draw(renderer, TextObject.literal(text), size, x, y, thickness, style, anchor);
    }

    @ApiStatus.Internal
    public void register() {
        final Identifier key = new Identifier("lmao_get_deleted_bro");
        if (key == null)
            throw new IllegalStateException("Expected font to be registered. (Is the mod accessing internal functions?)");
        this.info = BubbleBlaster.getInstance().loadFont(key);
    }

    @ApiStatus.Internal
    public BitmapFont getGdxFont(int size, Thickness thickness, FontStyle style) {
        return info.getFont(thickness, style);
    }

    @ApiStatus.Internal
    public BitmapFont getGdxFont(int size, Thickness thickness, FontStyle style, String langCode) {
        FontInfo fontInfo = alternatives.get(langCode);
        if (fontInfo == null) return null;
        BitmapFont font = fontInfo.getFont(thickness, style);
        return font;
    }

    public int height(int size) {
        return height(size, Thickness.REGULAR);
    }

    public int height(int size, int thickness) {
        return height(size, thickness, FontStyle.PLAIN);
    }

    public int height(int size, Thickness thickness) {
        return height(size, thickness, FontStyle.PLAIN);
    }

    public int height(int size, int thickness, FontStyle style) {
        return height(size, Thickness.closestTo(thickness), style);
    }

    public int height(int size, FontStyle style) {
        return height(size, Thickness.REGULAR, style);
    }

    public int height(int size, Thickness thickness, FontStyle style) {
        BitmapFont awtFont = getGdxFont(size, thickness, style);
        return (int) awtFont.getLineHeight();
    }

    public int width(int size, char c) {
        return width(size, c, Thickness.REGULAR);
    }

    public int width(int size, char c, int thickness) {
        return width(size, c, thickness, FontStyle.PLAIN);
    }

    public int width(int size, char c, Thickness thickness) {
        return width(size, c, thickness, FontStyle.PLAIN);
    }

    public int width(int size, char c, int thickness, FontStyle style) {
        return width(size, c, Thickness.closestTo(thickness), style);
    }

    public int width(int size, char c, FontStyle style) {
        return width(size, c, Thickness.REGULAR, style);
    }

    public int width(int size, char c, Thickness thickness, FontStyle style) {
        BitmapFont awtFont = getGdxFont(size, thickness, style);
        return (int) bounds(size, thickness, style, String.valueOf(c)).width;
    }

    public int width(int size, String text) {
        return width(size, text, Thickness.REGULAR);
    }

    public int width(int size, String text, int thickness) {
        return width(size, text, thickness, FontStyle.PLAIN);
    }

    public int width(int size, String text, Thickness thickness) {
        return width(size, text, thickness, FontStyle.PLAIN);
    }

    public int width(int size, String text, int thickness, FontStyle style) {
        return width(size, text, Thickness.closestTo(thickness), style);
    }

    public int width(int size, String text, FontStyle style) {
        return width(size, text, Thickness.REGULAR, style);
    }

    public int width(int size, String text, Thickness thickness, FontStyle style) {
        BitmapFont awtFont = getGdxFont(size, thickness, style);
        return (int) bounds(size, thickness, style, text).width;
    }

    public String wrap(int size, String text, int maxWidth) {
        return wrap(size, text, maxWidth, Thickness.REGULAR);
    }

    public String wrap(int size, String text, int maxWidth, int thickness) {
        return wrap(size, text, maxWidth, thickness, FontStyle.PLAIN);
    }

    public String wrap(int size, String text, int maxWidth, Thickness thickness) {
        return wrap(size, text, maxWidth, thickness, FontStyle.PLAIN);
    }

    public String wrap(int size, String text, int maxWidth, int thickness, FontStyle style) {
        return wrap(size, text, maxWidth, Thickness.closestTo(thickness), style);
    }

    public String wrap(int size, String text, int maxWidth, FontStyle style) {
        return wrap(size, text, maxWidth, Thickness.REGULAR, style);
    }

    public String wrap(int size, String text, int maxWidth, Thickness thickness, FontStyle style) {
        return String.join("\n", StringUtils.wrap(text, bitmapFont, new GlyphLayout(), maxWidth));
    }

}
