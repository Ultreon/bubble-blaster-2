package com.ultreon.bubbles.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.text.v1.TextObject;

@SuppressWarnings("unused")
public class GraphicsUtils {
    private static final ThreadLocal<GlyphLayout> glyphLayout = new ThreadLocal<>();

    public static void drawCenteredString(Renderer renderer, TextObject text, Vector2 pos, BitmapFont font, Color color) {
        GraphicsUtils.drawCenteredString(renderer, text.getText(), pos, font, color);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer    The Renderer instance.
     * @param text The String to draw.
     * @param pos The Rectangle to center the text within.
     * @param font The Font for the text.
     * @author XyperCode, <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawCenteredString(Renderer renderer, String text, Vector2 pos, BitmapFont font, Color color) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, text);

        // Determine the X coordinate for the text
        float x = pos.x - layout.width / 2;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        float y = pos.y - (layout.height + font.getDescent()) / 2;

        // Draw the String
        renderer.drawText(font, text, x, y, color);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The text to draw.
     * @param pos    The position to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawRightAnchoredString(Renderer renderer, String text, Vector2 pos, BitmapFont font, Color color) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        final var height = 0;

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) (pos.x - layout.width * 2);

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (pos.y + (height - layout.height) / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawText(font, text, x, y, color);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param pos  The Point to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredString(Renderer renderer, String text, Vector2 pos, BitmapFont font, Color color) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(font, text);

        final var height = 0;

        // Determine the X coordinate for the text
        int x = (int) pos.x;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (pos.y + (height - layout.height) / 2);

        // Draw the String
        renderer.drawText(font, text, x, y, color);
    }

}
