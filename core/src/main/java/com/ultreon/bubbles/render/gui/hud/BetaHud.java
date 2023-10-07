package com.ultreon.bubbles.render.gui.hud;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.translations.v1.Language;
import org.jetbrains.annotations.NotNull;

/**
 * The classic hud, the hud that's almost identical to older versions and editions create the game.
 * For example the Python versions. The only thing changed is how the status effects are shown.
 *
 * @see HudType
 */
public class BetaHud extends HudType {

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
    private final BitmapFont levelUpFont = Fonts.SANS_BETA_LEVEL_UP.get();
    private final BitmapFont infoFont = Fonts.SANS_PARAGRAPH_BOLD.get();
    private final BitmapFont fpsFont = Fonts.SANS_BETA_FPS.get();
    private long gameOverTime;

    /**
     * Renders hud.
     *
     * @param renderer renderer to use for drawing the hud.
     */
    @Override
    public void renderHudOverlay(Renderer renderer, World world, Gamemode gamemode, float deltaTime) {
        var game = BubbleBlaster.getInstance();
        var loadedGame = game.getLoadedGame();
        if (loadedGame == null) return;

        var player = gamemode.getPlayer();

        if (player == null) return;

        if (!world.isGameOver()) {
            this.preDrawTopBar(renderer, game);
            this.drawPlayerDetails(world, renderer, game, player);
            this.drawFpsCounter(renderer, game);
            this.postDrawTopBar(renderer, game);
        }

        this.drawMessages(renderer);

        this.drawLevelUpMessage(renderer, gamemode);
    }

    public void preDrawTopBar(Renderer renderer, BubbleBlaster game) {
        this.drawTopBar(renderer, game);
        this.drawHealthLineBackground(renderer, game);
        this.drawTopShade(renderer, game);
    }

    public void drawFpsCounter(Renderer renderer, BubbleBlaster game) {
        // Render FPS text.
        renderer.drawText(this.fpsFont, String.valueOf(game.getFps()), game.getWidth() - 10, 10, Color.argb(0x8000a5dc));
    }

    /**
     * Draw the game over message.
     *
     * @param renderer the renderer to draw with.
     * @param gamemode the game type bound to this hud.
     */
    public void drawLevelUpMessage(Renderer renderer, Gamemode gamemode) {
        renderer.setColor(LEVEL_UP_COLOR);

        if (this.showLevelUp && System.currentTimeMillis() > this.hideLevelUpTime) {
            this.showLevelUp = false;
            return;
        }

        // Level up message
        if (this.showLevelUp) {
            var text = "Level " + this.level;
            if (!text.equals(this.levelUpText)) {
                this.levelUpText = text;
                this.levelUpLayout.setText(this.levelUpFont, text);
            }

            var textWidth = this.levelUpLayout.width;
            var textHeight = this.levelUpLayout.height;

            var width = textWidth + 16;
            var height = textHeight + 16;

            var gameBounds = gamemode.getGameBounds();

            renderer.fill(
                    gameBounds.getX() + gameBounds.getWidth() - width / 2,
                    gameBounds.getY() + gameBounds.getHeight() - height / 2,
                    width, height, Color.BLACK.withAlpha(0x80)
            );

            renderer.drawTextCenter(this.levelUpFont, text, gameBounds.getX() + gameBounds.getWidth() / 2, gameBounds.getY() + gameBounds.getHeight() / 2, LEVEL_UP_COLOR);
        }
    }

    /**
     * Draw the player details.
     *
     * @param renderer    renderer to draw with.
     * @param game        the game instance.
     * @param player      the player to draw information for.
     */
    public void drawPlayerDetails(World world, @NotNull Renderer renderer, @NotNull BubbleBlaster game, @NotNull Player player) {
        // Assign colors for title and description.
        var titleColor = Color.rgb(255, 128, 0);
        var valueColor = Color.rgb(255, 255, 255);

        // As long the player isn't game over.
        if (world.isGameOver()) return;

        // As long the player exists.
        // Draw player components.
        this.drawStatusEffects(renderer, player);
        this.drawScoreText(renderer, player, titleColor, valueColor);
        this.drawLevelText(renderer, player, titleColor, valueColor);
        this.drawHealthLineForeground(renderer, game, player);
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
        var playerMaxDamage = player.getMaxHealth();
        var playerDamage = player.getHealth();

        // Calculate colors based on damage and max damage.
        playerDamage = Mth.clamp(playerDamage, 0, player.getMaxHealth());
        var max = playerMaxDamage / 2;
        if (playerDamage > playerMaxDamage / 2) {
            redValue = (int) ((max - (playerDamage - max)) * 255 / max);
            redValue = (int) Mth.clamp((double) redValue, 0, 255);
            greenValue = 255;
        } else {
            greenValue = (int) (playerDamage * 255 / (max / 2));
            greenValue = (int) Mth.clamp((double) greenValue, 0, 255);
            redValue = 255;
        }

        // Render health bar.
        renderer.setLineThickness(HEALTH_LINE_WIDTH);
        renderer.fill(0, 68, (int) (game.getWidth() * playerDamage / playerMaxDamage), 2, Color.rgb(redValue, greenValue, 0x20));
    }

    /**
     * Draws the level information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the level from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(World, Renderer, BubbleBlaster, Player)
     */
    public void drawLevelText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        renderer.drawTextCenter(this.infoFont, Language.translate("bubbleblaster.info.level"), 140, 20, titleColor);
        renderer.drawTextCenter(this.font, String.valueOf(player.getLevel()), 140, 50, valueColor);
    }

    /**
     * Draws the score information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the score from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(World, Renderer, BubbleBlaster, Player)
     */
    public void drawScoreText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        renderer.drawText(this.infoFont, Language.translate("bubbleblaster.info.score"), 70, 20, titleColor);
        renderer.drawText(this.font, String.valueOf((int) player.getScore()), 70, 50, valueColor);
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
            renderer.setTexture(BubbleBlaster.id("ui/effect_banner"));

            var i = 0;
            for (var appliedEffect : player.getActiveEffects()) {
                final var y = 16;
                final var x = 320 + i * 196;

                renderer.scissored(x, y, 192, 38, () -> {
                    // Format duration to string.
                    var time = appliedEffect.getRemainingTime().toSimpleString();

                    // EffectInstance bar.
                    renderer.blit(x, y, 192, 38);

                    // EffectInstance icon.
                    renderer.setTexture(appliedEffect.getType().getIconId());
                    renderer.blit(x + 5, y + 3, 32, 32);

                    // Time. 0:00:00
                    renderer.drawTextLeft(this.font, time, x + 56, y + 19.5f, Color.WHITE.withAlpha(0xC0));
                });

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
        var topBar = new Rectangle(0, 0, game.getWidth(), 70);
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
        renderer.setLineThickness(HEALTH_LINE_WIDTH);
        renderer.fill(0, 68, game.getWidth(), 2, HEALTH_LINE_BG_COLOR);
    }

    /**
     * Sets Game Over flag
     * Yes, as the title says: it sets the game over flag in the HUD.
     */
    public void gameOver() {

        // Values
        this.gameOverTime = System.currentTimeMillis();
    }

    @Override
    public void onLevelUp(int newLevel) {
        this.showLevelUp = true;
        this.level = newLevel;
        this.hideLevelUpTime = System.currentTimeMillis() + 3000;
    }

    public long getGameOverTime() {
        return this.gameOverTime;
    }
}
