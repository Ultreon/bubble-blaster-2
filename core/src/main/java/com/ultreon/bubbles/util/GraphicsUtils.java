package com.ultreon.bubbles.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.commons.v0.vector.Vec2d;
import com.ultreon.libs.commons.v0.vector.Vec2f;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.text.v0.TextObject;

@SuppressWarnings("unused")
public class GraphicsUtils {
    private static final ThreadLocal<GlyphLayout> glyphLayout = new ThreadLocal<>();

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer    The Renderer instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text within.
     * @param font The Font for the text.
     * @author XyperCode
     */
    public static void drawCenteredString(Renderer renderer, TextObject text, Rectangle rect, BitmapFont font) {
        drawCenteredString(renderer, text.getText(), rect, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer    The Renderer instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text within.
     * @param font The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawCenteredString(Renderer renderer, String text, Rectangle rect, BitmapFont font) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) (rect.getX() + (rect.getWidth() - layout.width) / 2);

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (rect.getY() + (rect.getHeight() - layout.height) / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawText(text, x, y);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer The renderer instance.
     * @param text The string text to draw.
     * @param pos The center of the text.
     * @param font The font for the text.
     * @author XyperCode
     */
    public static void drawCenteredString(Renderer renderer, TextObject text, Vector2 pos, BitmapFont font) {
        drawCenteredString(renderer, text.getText(), pos, font);
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
    public static void drawCenteredString(Renderer renderer, String text, Vector2 pos, BitmapFont font) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) (pos.x - layout.width / 2);

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (pos.y - layout.height / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawText(text, x, y);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer The Renderer instance.
     * @param text     The text to draw.
     * @param height   The height to center the text within.
     * @param font     The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawRightAnchoredString(Renderer renderer, String text, Vec2i pos, double height, BitmapFont font) {
        drawRightAnchoredString(renderer, text, pos.d(), height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer The Renderer instance.
     * @param text     The text to draw.
     * @param height   The height to center the text within.
     * @param font     The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawRightAnchoredString(Renderer renderer, String text, Vec2f pos, double height, BitmapFont font) {
        drawRightAnchoredString(renderer, text, pos.d(), height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The text to draw.
     * @param pos    The position to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawRightAnchoredString(Renderer renderer, String text, Vec2d pos, double height, BitmapFont font) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) (pos.getX() - layout.width * 2);

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (pos.getY() + (height - layout.height) / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawText(text, x, y);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredString(Renderer renderer, TextObject text, Vec2i point, double height, BitmapFont font) {
        drawLeftAnchoredString(renderer, text.getText(), point, height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredString(Renderer renderer, TextObject text, Vec2f point, double height, BitmapFont font) {
        drawLeftAnchoredString(renderer, text.getText(), point, height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredString(Renderer renderer, TextObject text, Vec2d point, double height, BitmapFont font) {
        drawLeftAnchoredString(renderer, text.getText(), point, height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredString(Renderer renderer, String text, Vec2i point, double height, BitmapFont font) {
        drawLeftAnchoredString(renderer, text, point.d(), height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredString(Renderer renderer, String text, Vec2f point, double height, BitmapFont font) {
        drawLeftAnchoredString(renderer, text, point.d(), height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredString(Renderer renderer, String text, Vec2d point, double height, BitmapFont font) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) point.getX();

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (point.getY() + (height - layout.height) / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawText(text, x, y);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredStringML(Renderer renderer, TextObject text, Vec2i point, double height, BitmapFont font) {
        drawLeftAnchoredStringML(renderer, text.getText(), point.d(), height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredStringML(Renderer renderer, TextObject text, Vec2f point, double height, BitmapFont font) {
        drawLeftAnchoredStringML(renderer, text.getText(), point.d(), height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredStringML(Renderer renderer, TextObject text, Vec2d point, double height, BitmapFont font) {
        drawLeftAnchoredStringML(renderer, text.getText(), point, height, font);
    }
    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredStringML(Renderer renderer, String text, Vec2i point, double height, BitmapFont font) {
        drawLeftAnchoredStringML(renderer, text, point.d(), height, font);
    }
    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredStringML(Renderer renderer, String text, Vec2f point, double height, BitmapFont font) {
        drawLeftAnchoredStringML(renderer, text, point.d(), height, font);
    }
    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    public static void drawLeftAnchoredStringML(Renderer renderer, String text, Vec2d point, double height, BitmapFont font) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) point.getX();

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (point.getY() + (height - layout.height) / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawMultiLineText(text, x, y);
    }
    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    @Deprecated
    public static void drawRightAnchoredString(Renderer renderer, String text, Vector2 point, double height, BitmapFont font) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) (point.x - layout.width * 2);

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (point.y + (height - layout.height) / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawText(text, x, y);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    @Deprecated
    public static void drawLeftAnchoredStringML(Renderer renderer, String text, Vector2 point, double height, BitmapFont font) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) point.x;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (point.y + (height - layout.height) / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawMultiLineText(text, x, y);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    @Deprecated
    public static void drawLeftAnchoredString(Renderer renderer, String text, Vector2 point, double height, BitmapFont font) {
        GlyphLayout layout = glyphLayout.get();
        if (layout == null) {
            layout = new GlyphLayout();
            glyphLayout.set(layout);
        }

        layout.setText(renderer.getFont(), text);

        // Determine the X coordinate for the text
        int x = (int) point.x;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top create the screen)
        int y = (int) (point.y + (height - layout.height) / 2);

        // Set the font
        renderer.setFont(font);

        // Draw the String
        renderer.drawText(text, x, y);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    @Deprecated
    public static void drawLeftAnchoredString(Renderer renderer, TextObject text, Vector2 point, double height, BitmapFont font) {
        drawLeftAnchoredString(renderer, text.getText(), point, height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    @Deprecated
    public static void drawLeftAnchoredStringML(Renderer renderer, TextObject text, Vector2 point, double height, BitmapFont font) {
        drawLeftAnchoredStringML(renderer, text.getText(), point, height, font);
    }

    /**
     * Draw a String centered in the middle create a Rectangle.
     *
     * @param renderer      The Renderer instance.
     * @param text   The String to draw.
     * @param point  The Point to center the text within.
     * @param height The height to center the text within.
     * @param font   The Font for the text.
     * @author <b>Danier Kvist</b> <a href="https://stackoverflow.com/a/27740330/11124294">from this answer</a>.
     */
    @Deprecated
    public static void drawRightAnchoredString(Renderer renderer, TextObject text, Vector2 point, double height, BitmapFont font) {
        drawRightAnchoredString(renderer, text.getText(), point, height, font);
    }

}
