package com.ultreon.bubbles.render.font;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.commons.util.StringUtils;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Font {
    protected final BubbleBlaster game = BubbleBlaster.getInstance();
    FontInfo info;

    public Font() {
        GameEvents.LOAD_FONTS.listen(loader -> {
            register();
        });
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
        draw(renderer, text.getText(), size, x, y, thickness, style, anchor);
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
        java.awt.Font awtFont = getAwtFont(size, thickness, style);
        FontMetrics metrics = renderer.fontMetrics(awtFont);

        switch (anchor) {
            case N -> {
                x = x - (float) metrics.stringWidth(text) / 2;
                y = y + (float) metrics.getHeight() / 2 + metrics.getDescent();
            }
            case NE -> {
                x = x - metrics.stringWidth(text);
                y = y + (float) metrics.getHeight() / 2 + metrics.getDescent();
            }
            case NW -> y = y + (float) metrics.getHeight() / 2 + metrics.getDescent();
            case E -> {
                x = x - metrics.stringWidth(text);
                y = y + metrics.getDescent();
            }
            case CENTER -> {
                x = x - (float) metrics.stringWidth(text) / 2;
                y = y + metrics.getDescent();
            }
            case W -> y = y + metrics.getDescent();
            case SE -> {
                x = x - metrics.stringWidth(text);
                y = y - (float) metrics.getHeight() / 2 + metrics.getAscent();
            }
            case S -> {
                x = x - (float) metrics.stringWidth(text) / 2;
                y = y - (float) metrics.getHeight() / 2 + metrics.getAscent();
            }
            case SW -> y = y - (float) metrics.getHeight() / 2 + metrics.getDescent();
        }

        renderer.font(getAwtFont(size, thickness, style));
        renderer.text(text, x, y);
    }

    @ApiStatus.Internal
    public void register() {
        Identifier key = Registry.FONTS.getKey(this);
        if (key == null)
            throw new IllegalStateException("Expected font to be registered. (Is the mod accessing internal functions?)");
        this.info = BubbleBlaster.getInstance().loadFont(key);
    }

    private java.awt.Font getAwtFont(int size, Thickness thickness, FontStyle style) {
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
