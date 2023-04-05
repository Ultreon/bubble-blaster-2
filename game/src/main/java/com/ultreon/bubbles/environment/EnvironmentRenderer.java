package com.ultreon.bubbles.environment;

import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.common.renderer.IRenderer;
import com.ultreon.bubbles.debug.Profiler;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.BufferRender;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class EnvironmentRenderer implements IRenderer {
    private static final Color UPPER_COLOR = Color.argb(0xff008EDA);
    private static final Color LOWER_COLOR = Color.argb(0xff004BA1);
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final Profiler profiler = game.profiler;
    private BufferedImage cached;

    public EnvironmentRenderer() {

    }

    /**
     * Draw bubble.
     * Draws bubble on screen.
     *
     * @param renderer      the graphics-2D instance
     * @param x      the x-coordinate.
     * @param y      the y-coordinate.
     * @param radius the bubble radius (full width.).
     * @param colors the bubble colors (on sequence).
     */
    public static void drawBubble(Renderer renderer, double x, double y, int radius, List<com.ultreon.bubbles.render.Color> colors) {
        // Define ellipse-depth (pixels).
        double i = 0f;

        // Loop colors.
        for (Color color : colors) {
            // Set stroke width.
            if (i == 0) {
                if (colors.size() > 1) {
                    renderer.stroke(new BasicStroke(1.4f));
                } else {
                    renderer.stroke(new BasicStroke(1.2f));
                }
            } else if (i == colors.size() - 1) {
                renderer.stroke(new BasicStroke(1.2f));
            } else {
                renderer.stroke(new BasicStroke(1.4f));
            }

            // Set color.
            renderer.color(color);

            // Draw ellipse.
            Ellipse2D ellipse = getEllipse(x, y, radius, i);
            renderer.outline(ellipse);

            // Add 2 to ellipse-depth (pixels).
            i += 1.2f;
        }
    }

    /**
     * Get Ellipse
     * Get ellipse from x, y, radius, delta-radius and delta-value.
     *
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param r the radius.
     * @param i the delta-radius.
     * @return the ellipse.
     */
    protected static Ellipse2D getEllipse(double x, double y, double r, double i) {
        return new Ellipse2D.Double(x + i, y + i, r - i * 2f, r - i * 2f);
    }

    @Nullable
    public Environment getEnvironment() {
        if (BubbleBlaster.getInstance() == null) {
            return null;
        }

        return BubbleBlaster.getInstance().environment;
    }

    @Override
    public void render(Renderer renderer) {
        Environment environment = getEnvironment();
        if (environment == null) return;
        if (environment.shuttingDown) return;

        profiler.section("Render BG", () -> {
            GameplayEvent currentGameplayEvent = environment.getCurrentGameEvent();
            if (currentGameplayEvent != null) {
                renderer.color(currentGameplayEvent.getBackgroundColor());
                renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
            } else {
                if (cached == null) {
                    BufferRender bufferRender = new BufferRender(new Dimension(game.getWidth(), game.getHeight()), game.getObserver());
                    Renderer buffered = bufferRender.getRenderer();
                    buffered.color(0xff006080);
                    buffered.paint(new GradientPaint(0f, 0f, UPPER_COLOR.toAwt(), 0f, BubbleBlaster.getInstance().getHeight(), LOWER_COLOR.toAwt()));
                    buffered.rect(0, 0, game.getWidth(), game.getHeight());
                    cached = bufferRender.done();
                }
                renderer.image(cached, 0, 0);
            }
        });

        profiler.section("Render Gamemode", () -> environment.getGamemode().render(renderer));

        profiler.section("Render Entities", () -> {
            for (Entity entity : environment.getEntities()) {
                if (entity.isVisible()) {
                    entity.render(renderer);
                }
            }
        });

        profiler.section("Render HUD", () -> {
            environment.getGamemode().renderGUI(renderer);
            environment.getGamemode().renderHUD(renderer);
        });
    }
}
