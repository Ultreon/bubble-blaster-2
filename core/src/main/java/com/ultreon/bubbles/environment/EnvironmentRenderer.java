package com.ultreon.bubbles.environment;

import com.badlogic.gdx.math.Circle;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.common.renderer.IRenderer;
import com.ultreon.bubbles.debug.Profiler;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.helpers.Mth;

import javax.annotation.Nullable;
import java.util.List;

public class EnvironmentRenderer implements IRenderer {
    private static final Color UPPER_COLOR = Color.argb(0xff008EDA);
    private static final Color LOWER_COLOR = Color.argb(0xff004BA1);
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final Profiler profiler = game.profiler;

    public EnvironmentRenderer() {

    }

    /**
     * Draw bubble.
     * Draws bubble on screen.
     *
     * @param renderer the graphics-2D instance
     * @param x        the x-coordinate.
     * @param y        the y-coordinate.
     * @param radius   the bubble radius (full width.).
     * @param colors   the bubble colors (on sequence).
     */
    public static void drawBubble(Renderer renderer, float x, float y, int radius,  List<Color> colors) {
        drawBubble(renderer, x, y, radius, 0, colors);
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
    public static void drawBubble(Renderer renderer, float x, float y, int radius, int destroyFrame, List<com.ultreon.bubbles.render.Color> colors) {
        // Define ellipse-depth (pixels).
        float i = 0f;
        var destroyFrame0 = Mth.clamp(destroyFrame, 0, 10);

        // Loop colors.
        for (Color color : colors) {
            // Set stroke width.
            if (i == 0) {
                if (colors.size() > 1) {
                    renderer.setLineWidth(1.4f);
                } else {
                    renderer.setLineWidth(1.2f);
                }
            } else if (i == colors.size() - 1) {
                renderer.setLineWidth(1.2f);
            } else {
                renderer.setLineWidth(1.4f);
            }

            // Set color.
            renderer.setColor(color);

            // Draw circle.
            Circle circle = getCircle(x, y, radius, i);
            renderer.circleLine(circle.x, circle.y, circle.radius);

            // Add 2 to circle-depth (pixels).
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
    protected static Circle getCircle(float x, float y, float r, float i) {
        return new Circle(x + i, y + i, r - i * 2f);
    }

    @Nullable
    public Environment getEnvironment() {
        if (BubbleBlaster.getInstance() == null) return null;

        return BubbleBlaster.getInstance().environment;
    }

    @Override
    public void render(Renderer renderer) {
        Environment environment = getEnvironment();
        if (environment == null) return;
        if (environment.shuttingDown) return;
        if (!environment.isInitialized()) return;

        profiler.section("Render BG", () -> {
            GameplayEvent currentGameplayEvent = environment.getCurrentGameEvent();
            if (currentGameplayEvent != null) {
                currentGameplayEvent.renderBackground(this.getEnvironment(), renderer);
            } else {
                renderer.fillGradient(0, 0, game.getWidth(), game.getHeight(), UPPER_COLOR, LOWER_COLOR);
                BubbleBlaster.getLogger().warn("Background not rendering");
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
