package com.ultreon.bubbles.gamemode;

import com.ultreon.libs.translations.v0.Language;
import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.Texture;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.commons.util.TimeUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * The classic hud, the hud that's almost identical to older versions and editions create the game.
 * For example the Python versions. The only thing changed is how the status effects are shown.
 *
 * @see GameHud
 */
public class ClassicModeHud extends GameHud {
    // Flags
    private boolean gameOver;

    // Strokes
    private static final Stroke HEALTH_LINE = new BasicStroke(1);

    // Colors
    private static final Color LEVEL_UP_COLOR = Color.rgb(0xffbb00);
    private static final Color TOP_BAR_BG_COLOR = Color.argb(0x7f000000);
    private static final Color HEALTH_LINE_BG_COLOR = Color.argb(0x7ffffff);
    private boolean showLevelUp;
    private long hideLevelUpTime;
    private int level;

    /**
     * Constructor create the hud, actually doesn't do much different from {@link GameHud}.
     *
     * @param gamemode game type bound to this hud.
     */
    public ClassicModeHud(Gamemode gamemode) {
        super(gamemode);
    }

    /**
     * Renders hud.
     *
     * @param renderer renderer to use for drawing the hud.
     */
    public void renderHUD(Renderer renderer) {
        BubbleBlaster game = BubbleBlaster.getInstance();
        LoadedGame loadedGame = game.getLoadedGame();
        if (loadedGame == null) return;

        Gamemode gamemode = loadedGame.getGamemode();
        Player player = gamemode.getPlayer();

        if (player == null) return;

        if (!gameOver) {
            preDrawTopBar(renderer, game);
            drawPlayerDetails(renderer, game, player);
            drawFpsCounter(renderer, game);
            postDrawTopBar(renderer, game);
        }

        drawMessages(renderer, game);

        drawLevelUpMessage(renderer, gamemode);
    }

    private void drawMessages(Renderer renderer, BubbleBlaster game) {
        LoadedGame loadedGame = game.getLoadedGame();
        if (loadedGame != null) {
            loadedGame.renderHUD(game, renderer);
        }
    }

    private void preDrawTopBar(Renderer renderer, BubbleBlaster game) {
        drawTopBar(renderer, game);
        drawHealthLineBackground(renderer, game);
        drawTopShade(renderer, game);
    }

    private void drawFpsCounter(Renderer renderer, BubbleBlaster game) {
        // Render FPS text.
        renderer.color(Color.argb(0x7f00a5dc));
        font.draw(renderer, ((Integer) game.getFps()).toString(), 12, game.getWidth() - 10, 10, Anchor.NE);
    }

    /**
     * Draw the game over message.
     *
     * @param renderer the renderer to draw with.
     * @param gamemode the game type bound to this hud.
     */
    private void drawLevelUpMessage(Renderer renderer, Gamemode gamemode) {
        renderer.color(LEVEL_UP_COLOR);

        if (showLevelUp && System.currentTimeMillis() > hideLevelUpTime) {
            showLevelUp = false;
            return;
        }

        // Game over message.
        if (showLevelUp) {
            String text = "Level " + level;

            int textWidth = font.width(50, text);
            int textHeight = font.height(50);

            int width = textWidth + 16;
            int height = textHeight + 16;

            Rectangle2D gameBounds = gamemode.getGameBounds();

            renderer.color(Color.argb(0x7f000000));

            renderer.rect(
                    (int)(gameBounds.getX() + gameBounds.getWidth() - width) / 2,
                    (int)(gameBounds.getY() + gameBounds.getHeight() - height) / 2, width, height);

            renderer.color(LEVEL_UP_COLOR);

            font.draw(renderer, text, 50, (float) (gameBounds.getX() + gameBounds.getWidth() / 2), (float) (gameBounds.getY() + gameBounds.getHeight() / 2), Thickness.BOLD, Anchor.S);
        }
    }

    /**
     * Draw the player details.
     *
     * @param renderer renderer to draw with.
     * @param game     the game instance.
     * @param player   the player to draw information for.
     */
    private void drawPlayerDetails(@NotNull Renderer renderer, @NotNull BubbleBlaster game, @NotNull Player player) {
        // Assign colors for title and description.
        Color titleColor = Color.rgb(255, 128, 0);
        Color valueColor = Color.rgb(255, 255, 255);

        // As long the player isn't game over.
        if (!gameOver) {
            // As long the player exists.
            // Draw player components.
            drawStatusEffects(renderer, game, player);
            drawScoreText(renderer, player, titleColor, valueColor);
            drawLevelText(renderer, player, titleColor, valueColor);
            drawHealthLineForeground(renderer, game, player);
        }
    }

    /**
     * Draws the foreground create the health line.
     * It's mostly the colored part in this case.
     *
     * @param renderer renderer to draw with.
     * @param game     the game instance.
     * @param player   the player, it's required to have it for the health display.
     * @see #drawHealthLineBackground(Renderer, BubbleBlaster)
     */
    private void drawHealthLineForeground(Renderer renderer, BubbleBlaster game, Player player) {
        // Prepare for health display.
        int greenValue;
        int redValue;
        double playerMaxDamage = player.getMaxHealth();
        double playerDamage = player.getHealth();

        // Calculate colors based on damage and max damage.
        playerDamage = Mth.clamp(playerDamage, 0, player.getMaxHealth());
        double max = playerMaxDamage / 2;
        if (playerDamage > playerMaxDamage / 2) {
            redValue = (int) ((max - (((playerDamage) - max))) * 255 / max);
            redValue = (int) Mth.clamp((double) redValue, 0, 255);
            greenValue = 255;
        } else {
            greenValue = (int) ((playerDamage) * 255 / (max / 2));
            greenValue = (int) Mth.clamp((double) greenValue, 0, 255);
            redValue = 255;
        }

        // Render health bar.
        renderer.stroke(HEALTH_LINE);
        renderer.color(Color.rgb(redValue, greenValue, 0));
        renderer.line(0, 69, (int) (game.getWidth() * playerDamage / playerMaxDamage), 69);
    }

    /**
     * Draws the level information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the level from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(Renderer, BubbleBlaster, Player)
     */
    private void drawLevelText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Level
        renderer.color(titleColor);
        font.draw(renderer, Language.translate("bubbles/Info/Level"), 18, 140, 20, Thickness.BOLD, Anchor.CENTER);
        renderer.color(valueColor);
        font.draw(renderer, String.valueOf(player.getLevel()), 14, 140, 50, Anchor.CENTER);
    }

    /**
     * Draws the score information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the score from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(Renderer, BubbleBlaster, Player)
     */
    private void drawScoreText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Score
        renderer.color(titleColor);
        font.draw(renderer, Language.translate("bubbles/Info/Score"), 18, 70, 20, Thickness.BOLD, Anchor.CENTER);
        renderer.color(valueColor);
        font.draw(renderer, String.valueOf((int) player.getScore()), 18, 70, 50, Anchor.CENTER);
    }

    /**
     * Draws the status effects. Showing icon, and time.
     *
     * @param renderer the renderer to draw with.
     * @param game     the game instance/
     * @param player   the player, to get the information about the status effects from.
     */
    private void drawStatusEffects(@NotNull Renderer renderer, @NotNull BubbleBlaster game, @NotNull Player player) {
        try {
            // EffectInstance image.
            Texture effectImage = game.getTextureManager().getOrLoadTexture(BubbleBlaster.id("ui/effect_banner"));

            int i = 0;
            for (AppliedEffect appliedEffect : player.getActiveEffects()) {
                // GraphicsProcessor 2D
                Renderer render = renderer.subInstance(320 + i * 196, 16, 192, 38);

                // Format duration to string.
                String time = TimeUtils.formatDuration(appliedEffect.getRemainingTime());

                // EffectInstance bar.
                effectImage.draw(render, 0, 0, 192, 38);

                // EffectInstance icon.
                renderer.texture(appliedEffect.getType().getIconId());
                appliedEffect.getType().getIcon().draw(renderer, 5, 3, 32, 32);
                render.color(Color.rgba(255, 255, 255, 192));

                // Time. 0:00:00
                font.draw(render, time, 16, 56, 19.5f, Thickness.BOLD, Anchor.W);
                render.dispose();

                // Next
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {

        }
    }

    /**
     * Draw the shadow create the top bar.
     *
     * @param renderer renderer to draw with.
     * @param game     game instance.
     */
    private void drawTopShade(Renderer renderer, BubbleBlaster game) {
        Rectangle2D topShade = new Rectangle2D.Double(0, 71, game.getWidth(), 30);
        Paint old = renderer.getPaint();

        GradientPaint paint = new GradientPaint(0f, 71f, Color.argb(0x30000000).toAwt(), 0f, 100f, Color.transparent.toAwt());
        renderer.paint(paint);
        renderer.fill(topShade);
        renderer.outline(topShade);
        renderer.paint(old);
    }

    /**
     * Draw the top bar background.
     *
     * @param renderer renderer to draw with.
     * @param game     game instance.
     */
    private void drawTopBar(Renderer renderer, BubbleBlaster game) {
        Rectangle2D topBar = new Rectangle2D.Double(0, 0, game.getWidth(), 70);

        // Top-bar.
        renderer.color(TOP_BAR_BG_COLOR);
        renderer.fill(topBar);
        renderer.outline(topBar);
    }

    /**
     * Post-drawing create the top bar background.
     *
     * @param renderer renderer to draw with.
     * @param game     game instance.
     */
    private void postDrawTopBar(Renderer renderer, BubbleBlaster game) {
        Rectangle2D topBar = new Rectangle2D.Double(0, 0, game.getWidth(), 70);
        Paint old = renderer.getPaint();

        // Gradient.
        GradientPaint gp = new GradientPaint(0f, 0f, Color.transparent.toAwt(), 0f, 70f, Color.argb(0x18000000).toAwt());
        renderer.paint(gp);
        renderer.fill(topBar);
        renderer.outline(topBar);
        renderer.paint(old);
    }

    /**
     * Draw health line background. (The grayish part)
     *
     * @param renderer the renderer to draw with.
     * @param game     the game instance.
     * @see #drawHealthLineForeground(Renderer, BubbleBlaster, Player)
     */
    private void drawHealthLineBackground(Renderer renderer, BubbleBlaster game) {
        // Health line.
        renderer.stroke(HEALTH_LINE);
        renderer.color(HEALTH_LINE_BG_COLOR);
        renderer.line(0, 69, game.getWidth(), 69);
    }

    /**
     * Sets Game Over flag
     * Yes, as the title says: it sets the game over flag in the HUD.
     */
    public void setGameOver() {
        gameOver = true;
        // Values
        long gameOverTime = System.currentTimeMillis();
    }

    @Override
    public void onLevelUp(int to) {
        this.showLevelUp = true;
        this.level = to;
        this.hideLevelUpTime = System.currentTimeMillis() + 3000;
    }
}
