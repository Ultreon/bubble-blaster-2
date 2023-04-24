package com.ultreon.bubbles.render.font;

import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.commons.util.StringUtils;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.text.v0.TextObject;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Font {
    protected final BubbleBlaster game = BubbleBlaster.getInstance();
    protected final Map<String, FontInfo> alternatives = new HashMap<>();

    FontInfo info;

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
        java.awt.Font awtFont = getAwtFont(size, thickness, style);
        AttributedString attrString = text.getAttrString();
        if (text.getText().isEmpty()) return;

        java.awt.Font fallback = getAwtFont(size, thickness, style, GameSettings.instance().getLanguage());
        java.awt.Font font = getAwtFont(size, thickness, style);
        attrString.addAttribute(TextAttribute.FONT, font);
        attrString.addAttribute(TextAttribute.SIZE, size);
        attrString.addAttribute(TextAttribute.FAMILY, font.getFamily());
        attrString.addAttribute(TextAttribute.LIGATURES, true);

        var bounds = bounds(renderer, attrString);

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
//        renderer.font(font);
        renderer.text(attrString, x, y);
    }

    private Rectangle2D.Float bounds(Renderer renderer, AttributedString text) {
        TextLayout textLayout = new TextLayout(
                text.getIterator(),
                renderer.getFontRenderContext()
        );
        return (Rectangle2D.Float) textLayout.getBounds();
    }

    public Rectangle2D.Float bounds(Renderer renderer, int size,  FontStyle style, TextObject text) {
        return bounds(renderer, size, Thickness.REGULAR, style, text);
    }

    public Rectangle2D.Float bounds(Renderer renderer, int size, TextObject text) {
        return bounds(renderer, size, Thickness.REGULAR, text);
    }

    public Rectangle2D.Float bounds(Renderer renderer, int size, Thickness thickness, TextObject text) {
        return bounds(renderer, size, thickness, FontStyle.PLAIN, text);
    }

    public Rectangle2D.Float bounds(Renderer renderer, int size, Thickness thickness, FontStyle style, TextObject text) {
        AttributedString attrString = text.getAttrString();

        java.awt.Font font = getAwtFont(size, thickness, style);
        attrString.addAttribute(TextAttribute.FONT, font);
        attrString.addAttribute(TextAttribute.SIZE, size);
        attrString.addAttribute(TextAttribute.FAMILY, font.getFamily());
        attrString.addAttribute(TextAttribute.LIGATURES, true);

        return bounds(renderer, attrString);
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
        Identifier key = Registries.FONTS.getKey(this);
        if (key == null)
            throw new IllegalStateException("Expected font to be registered. (Is the mod accessing internal functions?)");
        this.info = BubbleBlaster.getInstance().loadFont(key);
    }

    @ApiStatus.Internal
    public java.awt.Font getAwtFont(int size, Thickness thickness, FontStyle style) {
        java.awt.Font font = info.getFont(thickness, style);
        if (thickness == Thickness.BOLD) {
            if (style == FontStyle.ITALIC) {
                return new java.awt.Font(font.getName(), java.awt.Font.BOLD + java.awt.Font.ITALIC, size);
            }
            return new java.awt.Font(font.getName(), java.awt.Font.BOLD, size);
        } else if (style == FontStyle.ITALIC) {
            return new java.awt.Font(font.getName(), java.awt.Font.ITALIC, size);
        }
        return new java.awt.Font(font.getName(), java.awt.Font.PLAIN, size);
    }

    @ApiStatus.Internal
    public java.awt.Font getAwtFont(int size, Thickness thickness, FontStyle style, String langCode) {
        FontInfo fontInfo = alternatives.get(langCode);
        if (fontInfo == null) return null;
        java.awt.Font font = fontInfo.getFont(thickness, style);
        if (thickness == Thickness.BOLD) {
            if (style == FontStyle.ITALIC) {
                return new java.awt.Font(font.getName(), java.awt.Font.BOLD + java.awt.Font.ITALIC, size);
            }
            return new java.awt.Font(font.getName(), java.awt.Font.BOLD, size);
        } else if (style == FontStyle.ITALIC) {
            return new java.awt.Font(font.getName(), java.awt.Font.ITALIC, size);
        }
        return new java.awt.Font(font.getName(), java.awt.Font.PLAIN, size);
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
        java.awt.Font awtFont = getAwtFont(size, thickness, style);
        return game.getFontMetrics(awtFont).getHeight();
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
        java.awt.Font awtFont = getAwtFont(size, thickness, style);
        return game.getFontMetrics(awtFont).charWidth(c);
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
        java.awt.Font awtFont = getAwtFont(size, thickness, style);
        return game.getFontMetrics(awtFont).stringWidth(text);
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
        java.awt.Font awtFont = getAwtFont(size, thickness, style);
        return String.join("\n", StringUtils.wrap(text, BubbleBlaster.getInstance().getFontMetrics(awtFont), maxWidth));
    }

}
