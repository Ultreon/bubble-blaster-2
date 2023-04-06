package com.ultreon.bubbles.render.font;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Renderer;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.Set;

public class Font {
    private String name;
    private BubbleBlaster game = BubbleBlaster.getInstance();

    public Font() {
        GameEvents.LOAD_FONTS.listen(loader -> {
            BubbleBlaster.getLogger().error("loader = " + loader);
            register();
        });
    }

    public void drawString(Renderer renderer, TextObject text, int size, float x, float y) {
        drawString(renderer, text, size, x, y, Anchor.NW);
    }

    public void drawString(Renderer renderer, String text, int size, float x, float y) {
        drawString(renderer, text, size, x, y, Anchor.NW);
    }

    public void drawString(Renderer renderer, TextObject text, int size, float x, float y, Anchor anchor) {
        drawString(renderer, text.getText(), size, x, y, anchor);
    }

    public void drawString(Renderer renderer, String text, int size, float x, float y, Anchor anchor) {
        java.awt.Font awtFont = getAwtFont(size);
        // Get the FontMetrics
        FontMetrics metrics = renderer.fontMetrics(awtFont);

        switch (anchor) {
            case N -> x = x - (float) metrics.stringWidth(text) / 2;
            case NE -> x = x - metrics.stringWidth(text);
            case W -> y = y - (float) metrics.getHeight() / 2 + metrics.getAscent();
            case CENTER -> {
                x = x - (float) metrics.stringWidth(text) / 2;
                y = y - (float) metrics.getHeight() / 2 + metrics.getAscent();
            }
            case E -> {
                x = x - metrics.stringWidth(text);
                y = y - (float) metrics.getHeight() / 2 + metrics.getAscent();
            }
            case SW -> y = y - metrics.getHeight() + metrics.getAscent();
            case S -> {
                x = x - (float) metrics.stringWidth(text) / 2;
                y = y - metrics.getHeight() + metrics.getAscent();
            }
            case SE -> {
                x = x - metrics.stringWidth(text);
                y = y - metrics.getHeight() + metrics.getAscent();
            }
        }

        // Set the font
        renderer.font(getAwtFont(size));

        // Draw the text
        renderer.text(text, x, y);
    }

    @ApiStatus.Internal
    public void register() {
        Identifier key = Registry.FONTS.getKey(this);
        if (key == null) throw new IllegalStateException("Expected font to be registered. (Is the mod accessing internal functions?)");
        java.awt.Font awtFont = BubbleBlaster.getInstance().loadFont(key);
        name = awtFont.getName();
        System.out.println("name = " + name);
    }

    @SuppressWarnings("MagicConstant")
    private java.awt.Font getAwtFont(int size, Style... styles) {
        return new java.awt.Font(name, mergeStyle(styles), size);
    }

    private int mergeStyle(Style[] styles) {
        return Set.of(styles).stream().mapToInt(value -> value.awtIndex).sum();
    }

    public int height(int size) {
        java.awt.Font awtFont = getAwtFont(size);
        return game.getFontMetrics(awtFont).getHeight();
    }

    public int width(int size, char c) {
        java.awt.Font awtFont = getAwtFont(size);
        return game.getFontMetrics(awtFont).charWidth(c);
    }

    public int width(int size, String text) {
        java.awt.Font awtFont = getAwtFont(size);
        return game.getFontMetrics(awtFont).stringWidth(text);
    }

    public enum Style {
        PLAIN(0), BOLD(1), ITALIC(2);

        private final int awtIndex;

        Style(int awtIndex) {

            this.awtIndex = awtIndex;
        }
    }
}
