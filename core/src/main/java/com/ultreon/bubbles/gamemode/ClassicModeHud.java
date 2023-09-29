package com.ultreon.bubbles.gamemode;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.commons.util.TimeUtils;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.libs.translations.v0.Language;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Rectangle2D;

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
    private static final float HEALTH_LINE_WIDTH = 1.0f;

    // Colors
    private static final Color LEVEL_UP_COLOR = Color.rgb(0xffbb00);
    private static final Color TOP_BAR_BG_COLOR = Color.argb(0x7f000000);
    private static final Color HEALTH_LINE_BG_COLOR = Color.argb(0x7ffffff);
    private boolean showLevelUp;
    private long hideLevelUpTime;
    private int level;
    private String levelUpText;
    private final GlyphLayout levelUpLayout = new GlyphLayout();
    private final BitmapFont levelUpFont = Fonts.SANS_REGULAR_40.get();
    private final BitmapFont infoFont = Fonts.SANS_BOLD_14.get();
    private long gameOverTime;

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

    public void drawMessages(Renderer renderer, BubbleBlaster game) {
        LoadedGame loadedGame = game.getLoadedGame();
        if (loadedGame != null) {
            loadedGame.renderHUD(game, renderer);
        }
    }

    public void preDrawTopBar(Renderer renderer, BubbleBlaster game) {
        drawTopBar(renderer, game);
        drawHealthLineBackground(renderer, game);
        drawTopShade(renderer, game);
    }

    public void drawFpsCounter(Renderer renderer, BubbleBlaster game) {
        // Render FPS text.
        renderer.setColor(Color.argb(0x7f00a5dc));
        renderer.drawText(((Integer) game.getFps()).toString(), game.getWidth() - 10, 10, Anchor.NE);
    }

    /**
     * Draw the game over message.
     *
     * @param renderer the renderer to draw with.
     * @param gamemode the game type bound to this hud.
     */
    public void drawLevelUpMessage(Renderer renderer, Gamemode gamemode) {
        renderer.setColor(LEVEL_UP_COLOR);

        if (showLevelUp && System.currentTimeMillis() > hideLevelUpTime) {
            showLevelUp = false;
            return;
        }

        // Level up message
        if (showLevelUp) {
            String text = "Level " + level;
            if (!text.equals(levelUpText)) {
                levelUpText = text;
                levelUpLayout.setText(levelUpFont, text);
            }

            float textWidth = levelUpLayout.width;
            float textHeight = levelUpLayout.height;

            float width = textWidth + 16;
            float height = textHeight + 16;

            Rectangle2D gameBounds = gamemode.getGameBounds();

            renderer.setColor(Color.argb(0x7f000000));

            renderer.fill(
                    (float) (gameBounds.getX() + gameBounds.getWidth() - width / 2),
                    (float) (gameBounds.getY() + gameBounds.getHeight() - height / 2), width, height);

            renderer.setColor(LEVEL_UP_COLOR);

            renderer.drawCenteredText(levelUpFont, text, (float) (gameBounds.getX() + gameBounds.getWidth() / 2), (float) (gameBounds.getY() + gameBounds.getHeight() / 2));
        }
    }

    /**
     * Draw the player details.
     *
     * @param renderer renderer to draw with.
     * @param game     the game instance.
     * @param player   the player to draw information for.
     */
    public void drawPlayerDetails(@NotNull Renderer renderer, @NotNull BubbleBlaster game, @NotNull Player player) {
        // Assign colors for title and description.
        Color titleColor = Color.rgb(255, 128, 0);
        Color valueColor = Color.rgb(255, 255, 255);

        // As long the player isn't game over.
        if (!gameOver) {
            // As long the player exists.
            // Draw player components.
            drawStatusEffects(renderer, player);
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
    public void drawHealthLineForeground(Renderer renderer, BubbleBlaster game, Player player) {
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
        renderer.setLineWidth(HEALTH_LINE_WIDTH);
        renderer.setColor(Color.rgb(redValue, greenValue, 0));
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
    public void drawLevelText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Level
        renderer.setColor(titleColor);
        renderer.drawText(infoFont, Language.translate("bubbleblaster/Info/Level"), 140, 20, Anchor.CENTER);
        renderer.setColor(valueColor);
        renderer.drawText(font, String.valueOf(player.getLevel()), 140, 50, Anchor.CENTER);
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
    public void drawScoreText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Score
        renderer.setColor(titleColor);
        renderer.drawText(infoFont, Language.translate("bubbleblaster/Info/Score"), 70, 20, Anchor.CENTER);
        renderer.setColor(valueColor);
        renderer.drawText(font, String.valueOf((int) player.getScore()), 70, 50, Anchor.CENTER);
    }

    /**
     * Draws the status effects. Showing icon, and time.
     *
     * @param renderer the renderer to draw with.
     * @param player   the player, to get the information about the status effects from.
     */
    public void drawStatusEffects(@NotNull Renderer renderer, @NotNull Player player) {
        try {
            // EffectInstance image.
            renderer.blit(BubbleBlaster.id("ui/effect_banner"));

            int i = 0;
            for (StatusEffectInstance appliedEffect : player.getActiveEffects()) {
                // Renderer 2D
                renderer.subInstance(320 + i * 196, 16, 192, 38, render -> {
                    // Format duration to string.
                    String time = TimeUtils.formatDuration(appliedEffect.getRemainingTime());

                    // EffectInstance bar.
                    render.blit(0, 0, 192, 38);

                    // EffectInstance icon.
                    render.blit(appliedEffect.getType().getIconId());
                    render.blit(5, 3, 32, 32);
                    render.setColor(Color.rgba(255, 255, 255, 192));

                    // Time. 0:00:00
                    renderer.drawLeftAnchoredText(font, time, 56, 19.5f);
                });
                // Next
                i++;
            }
        } catch (NullPointerException ignored) {

        }
    }

    /**
     * Draw the shadow create the top bar.
     *
     * @param renderer renderer to draw with.
     * @param game     game instance.
     */
    public void drawTopShade(Renderer renderer, BubbleBlaster game) {
        renderer.fillGradient(0, 71, game.getWidth(), 30, Color.argb(0x30000000), Color.TRANSPARENT);
    }

    /**
     * Draw the top bar background.
     *
     * @param renderer renderer to draw with.
     * @param game     game instance.
     */
    public void drawTopBar(Renderer renderer, BubbleBlaster game) {
        Rectangle topBar = new Rectangle(0, 0, game.getWidth(), 70);
        renderer.fill(topBar, TOP_BAR_BG_COLOR);
    }

    /**
     * Post-drawing create the top bar background.
     *
     * @param renderer renderer to draw with.
     * @param game     game instance.
     */
    public void postDrawTopBar(Renderer renderer, BubbleBlaster game) {
        renderer.fillGradient(0, 0, game.getWidth(), 70, Color.TRANSPARENT, Color.argb(0x18000000));
    }

    /**
     * Draw health line background. (The grayish part)
     *
     * @param renderer the renderer to draw with.
     * @param game     the game instance.
     * @see #drawHealthLineForeground(Renderer, BubbleBlaster, Player)
     */
    public void drawHealthLineBackground(Renderer renderer, BubbleBlaster game) {
        // Health line.
        renderer.setLineWidth(HEALTH_LINE_WIDTH);
        renderer.setColor(HEALTH_LINE_BG_COLOR);
        renderer.line(0, 69, game.getWidth(), 69);
    }

    /**
     * Sets Game Over flag
     * Yes, as the title says: it sets the game over flag in the HUD.
     */
    public void setGameOver() {
        gameOver = true;

        // Values
        gameOverTime = System.currentTimeMillis();
    }

    @Override
    public void onLevelUp(int to) {
        this.showLevelUp = true;
        this.level = to;
        this.hideLevelUpTime = System.currentTimeMillis() + 3000;
    }

    public long getGameOverTime() {
        return gameOverTime;
    }
}
