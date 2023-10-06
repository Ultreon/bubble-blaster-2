package com.ultreon.bubbles.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.debug.Profiler;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.event.v1.RenderEvents;
import com.ultreon.bubbles.init.StatusEffects;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderable;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.hud.HudType;
import com.ultreon.libs.commons.v0.Mth;

import javax.annotation.Nullable;
import java.util.List;

public class WorldRenderer implements Renderable {
    public static final Color BG_TOP = Color.argb(0xff008EDA);
    public static final Color BG_BOTTOM = Color.argb(0xff004BA1);
    private static VfxFrameBuffer worldFbo;
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private final Profiler profiler = this.game.profiler;

    public WorldRenderer() {

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
    @Deprecated(forRemoval = true)
    public static void drawBubble(Renderer renderer, float x, float y, float radius, List<Color> colors) {
        WorldRenderer.drawBubble(renderer, x, y, radius, colors, null);
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
    @Deprecated(forRemoval = true)
    public static void drawBubble(Renderer renderer, float x, float y, float radius, List<Color> colors, @Nullable Texture insideTexture) {
        WorldRenderer.drawBubble(renderer, x, y, radius, 0, colors, insideTexture);
    }

    /**
     * Draw bubble.
     * Draws bubble on screen.
     *
     * @param renderer     the graphics-2D instance
     * @param x            the x-coordinate.
     * @param y            the y-coordinate.
     * @param radius       the bubble radius (full width.).
     * @param destroyFrame
     * @param colors       the bubble colors (on sequence).
     */
    @Deprecated(forRemoval = true)
    public static void drawBubble(Renderer renderer, float x, float y, float radius, int destroyFrame, List<Color> colors) {
        WorldRenderer.drawBubble(renderer, x, y, radius, destroyFrame, colors, null);
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
    @Deprecated(forRemoval = true)
    public static void drawBubble(Renderer renderer, float x, float y, float radius, int destroyFrame, List<Color> colors, Texture insideTexture) {
        // Define ellipse-depth (pixels).
        float i = 0f;
        int destroyFrame0 = Mth.clamp(destroyFrame, 0, 10);

        float thickness = BubbleBlasterConfig.BUBBLE_LINE_THICKNESS.get();

        // Loop colors.
        for (Color color : colors) {
            renderer.setLineThickness(thickness + 1 / 2f);

            // Draw singular circle in the circle list.
            Circle circle = WorldRenderer.getCircle(x, y, radius, i);
            renderer.circle(circle.x, circle.y, circle.radius, color);
            if (insideTexture != null) {
                renderer.setTexture(insideTexture);
                renderer.blit((int) (circle.x - circle.radius / 4), (int) (circle.y - circle.radius / 4), (int) circle.radius / 2, (int) circle.radius / 2);
            }

            i += thickness;
        }
    }

    /**
     * Draw bubble.
     * Draws bubble on screen.
     *
     * @param renderer      the graphics-2D instance
     * @param x      the x-coordinate.
     * @param y      the y-coordinate.
     * @param radius the bubble radius (full width.).
     * @param type   the type of bubble to render.
     */
    public static void drawBubble(Renderer renderer, float x, float y, float radius, int destroyFrame, BubbleType type) {
        // Define ellipse-depth (pixels).
        float i = 0f;
        int destroyFrame0 = Mth.clamp(destroyFrame, 0, 10);

        float thickness = BubbleBlasterConfig.BUBBLE_LINE_THICKNESS.get();

        // Loop colors.
        for (Color color : type.getColors()) {
            renderer.setLineThickness(thickness + 1 / 2f);

            // Draw singular circle in the circle list.
            Circle circle = WorldRenderer.getCircle(x, y, radius, i);
            renderer.circle(circle.x, circle.y, circle.radius, color);
            Texture insideTexture = type.getInsideTexture();
            if (insideTexture != null) {
                renderer.setTexture(insideTexture);
                renderer.blit((int) (circle.x - circle.radius / 4), (int) (circle.y - circle.radius / 4), (int) circle.radius / 2, (int) circle.radius / 2);
            }

            i += thickness;
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
        return new Circle(x, y, r - i * 2f);
    }

    public static VfxFrameBuffer getWorldFbo() {
        return worldFbo;
    }

    @Nullable
    public World getWorld() {
        if (BubbleBlaster.getInstance() == null) return null;

        return BubbleBlaster.getInstance().world;
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        World world = this.getWorld();
        if (world == null) return;
        if (world.shuttingDown) return;
        if (!world.isInitialized()) return;

        HudType hudOverride = this.getWorld().getGamemode().getHudOverride();
        HudType hud = hudOverride != null ? hudOverride : HudType.getCurrent();

//        renderer.enableNoise();

        this.profiler.section("Render BG", () -> this.renderBackground(renderer, world, hud));
        this.profiler.section("Render Entities", () -> this.renderEntities(renderer, world));

        Player player = world.getPlayer();
        StatusEffectInstance blindness = player.getActiveEffect(StatusEffects.BLINDNESS);
        if (blindness != null) {
            int strength = blindness.getStrength();
            int alpha = Mth.clamp((strength + 1) * 0x10 + 0x60, 0, 255);
            renderer.fill(0, 0, this.game.getWidth(), this.game.getHeight(), Color.BLACK.withAlpha(alpha));
        }

        this.profiler.section("Render HUD", () -> hud.renderHudOverlay(renderer, world, world.getGamemode(), deltaTime));

//        renderer.disableNoise();
    }

    private void renderBackground(Renderer renderer, World world, HudType hud) {
        GameplayEvent currentGameplayEvent = world.getActiveEvent();
        if (currentGameplayEvent != null) {
            currentGameplayEvent.renderBackground(this.getWorld(), renderer);
        } else {
            if (!hud.renderBackground(renderer, BG_TOP, BG_BOTTOM)) {
                renderer.fillGradient(0, 0, this.game.getWidth(), this.game.getHeight(), BG_TOP, BG_BOTTOM);
            }
        }
    }

    private void renderEntities(Renderer renderer, World world) {
        for (Entity entity : world.getEntities()) {
            if (entity.isVisible()) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    RenderEvents.RENDER_PLAYER_BEFORE.factory().onRenderPlayerBefore(player, renderer);
                }
                RenderEvents.RENDER_ENTITY_BEFORE.factory().onRenderEntityBefore(entity, renderer);
                entity.render(renderer);
                RenderEvents.RENDER_ENTITY_AFTER.factory().onRenderEntityAfter(entity, renderer);
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    RenderEvents.RENDER_PLAYER_AFTER.factory().onRenderPlayerAfter(player, renderer);
                }
            }
            if (this.game.isCollisionShapesShown()) {
                Shape2D shape = entity.getShape();
                renderer.setLineThickness(6.0f);
                renderer.outline(shape, Color.BLACK);
                renderer.setLineThickness(2.0f);
                renderer.outline(shape, Color.WHITE);
            }
        }
    }
}
