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
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
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
public class LegacyHud extends ClassicModeHud {
    // Flags
    private boolean gameOver;

    // Colors
    private static final Color LEVEL_UP_COLOR = Color.ORANGE;
    private static final Color TOP_BAR_BG_COLOR = Color.rgb(0x008b8b);
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
    public LegacyHud(Gamemode gamemode) {
        super(gamemode);
    }

    /**
     * Renders hud.
     *
     * @param renderer renderer to use for drawing the hud.
     */
    @Override
    public void renderHUD(Renderer renderer) {
        BubbleBlaster game = BubbleBlaster.getInstance();
        LoadedGame loadedGame = game.getLoadedGame();
        if (loadedGame == null) return;

        Gamemode gamemode = loadedGame.getGamemode();
        Player player = gamemode.getPlayer();

        if (player == null) return;

        if (!gameOver) {
            drawTopBar(renderer, game);
            drawPlayerDetails(renderer, game, player);
        }

        drawLevelUpMessage(renderer, gamemode);
    }

    @Override
    public void drawMessages(Renderer renderer, BubbleBlaster game) {
        // Legacy doesn't have this
    }

    /**
     * Draw the game over message.
     *
     * @param renderer the renderer to draw with.
     * @param gamemode the game type bound to this hud.
     */
    @Override
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

            Rectangle2D gameBounds = gamemode.getGameBounds();

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
    @Override
    public void drawPlayerDetails(@NotNull Renderer renderer, @NotNull BubbleBlaster game, @NotNull Player player) {
        // Assign colors for title and description.
        Color titleColor = Color.WHITE;
        Color valueColor = Color.CYAN;

        // As long the player isn't game over.
        if (!this.gameOver) {
            // As long the player exists.
            // Draw player components.
            drawStatusEffects(renderer, player);
            drawScoreText(renderer, player, titleColor, valueColor);
            drawLevelText(renderer, player, titleColor, valueColor);
            drawSpeedText(renderer, player, titleColor, valueColor);
            drawLivesText(renderer, player, titleColor, valueColor);
        }
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
    @Override
    public void drawScoreText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Score
        renderer.setColor(titleColor);
        renderer.drawCenteredText(infoFont, Language.translate("bubbleblaster/Info/Score"), 70, 30);
        renderer.setColor(valueColor);
        renderer.drawCenteredText(font, String.valueOf((int) player.getScore()), 70, 60);
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
    @Override
    public void drawLevelText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Level
        renderer.setColor(titleColor);
        renderer.drawCenteredText(infoFont, Language.translate("bubbleblaster/Info/Level"), 140, 30);
        renderer.setColor(valueColor);
        renderer.drawCenteredText(font, String.valueOf(player.getLevel()), 140, 60);
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
    public void drawSpeedText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Score
        renderer.setColor(titleColor);
        renderer.drawCenteredText(infoFont, Language.translate("bubbleblaster/Info/Speed"), 210, 30);
        renderer.setColor(valueColor);
        renderer.drawCenteredText(font, String.valueOf(player.getSpeed()), 210, 60);
    }

    /**
     * Draws the legacy health information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the level from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(Renderer, BubbleBlaster, Player)
     */
    public void drawLivesText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Level
        renderer.setColor(titleColor);
        renderer.drawCenteredText(infoFont, Language.translate("bubbleblaster/Info/Lives"), 280, 30);
        renderer.setColor(valueColor);
        renderer.drawCenteredText(font, String.valueOf((int)(10 * player.getHealth() / player.getMaxHealth())), 280, 60);
    }

    /**
     * Draws the status effects. Showing icon, and time.
     *
     * @param renderer the renderer to draw with.
     * @param player   the player, to get the information about the status effects from.
     */
    @Override
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
     * Draw the top bar background.
     *
     * @param renderer renderer to draw with.
     * @param game     game instance.
     */
    @Override
    public void drawTopBar(Renderer renderer, BubbleBlaster game) {
        Rectangle topBar = new Rectangle(0, 0, game.getWidth(), 70);
        renderer.fill(topBar, TOP_BAR_BG_COLOR);
        renderer.setColor(Color.rgb(0xadd8e6));
        renderer.setLineWidth(1);
        renderer.box(0, 71, game.getWidth(), 1, Color.rgb(0xadd8e6), new Insets(1));
    }

    /**
     * Sets Game Over flag
     * Yes, as the title says: it sets the game over flag in the HUD.
     */
    @Override
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

    @Override
    public long getGameOverTime() {
        return gameOverTime;
    }
}
