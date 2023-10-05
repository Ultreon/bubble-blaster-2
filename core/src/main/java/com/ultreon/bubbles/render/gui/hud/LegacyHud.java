package com.ultreon.bubbles.render.gui.hud;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.util.TimeUtils;
import com.ultreon.libs.translations.v1.Language;
import org.jetbrains.annotations.NotNull;

/**
 * The classic hud, the hud that's almost identical to older versions and editions create the game.
 * For example the Python versions. The only thing changed is how the status effects are shown.
 *
 * @see HudType
 */
public class LegacyHud extends HudType {
    private static final Color BACKGROUND_COLOR = Color.rgb(0x00a7a7);

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

    /**
     * Renders hud.
     *
     * @param renderer renderer to use for drawing the hud.
     */
    @Override
    public void renderHudOverlay(Renderer renderer, World world, Gamemode gamemode, float deltaTime) {
        LoadedGame loadedGame = this.game.getLoadedGame();
        if (loadedGame == null) return;

        Player player = gamemode.getPlayer();

        if (player == null) return;

        if (!world.isGameOver()) {
            this.drawTopBar(renderer, this.game);
            this.drawPlayerDetails(world, renderer, player);
        }

        this.drawLevelUpMessage(renderer, gamemode);
    }

    @Override
    public void drawMessages(Renderer renderer) {
        // Legacy doesn't have this
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
            String text = "Level " + this.level;
            if (!text.equals(this.levelUpText)) {
                this.levelUpText = text;
                this.levelUpLayout.setText(this.levelUpFont, text);
            }

            Rectangle gameBounds = gamemode.getGameBounds();

            renderer.drawTextCenter(this.levelUpFont, text, gameBounds.getX() + gameBounds.getWidth() / 2, gameBounds.getY() + gameBounds.getHeight() / 2, LEVEL_UP_COLOR);
        }
    }

    /**
     * Draw the player details.
     *
     * @param world the world
     * @param renderer    renderer to draw with.
     * @param player      the player to draw information for.
     */
    public void drawPlayerDetails(@NotNull World world, @NotNull Renderer renderer, @NotNull Player player) {
        // Assign colors for title and description.
        Color titleColor = Color.WHITE;
        Color valueColor = Color.CYAN;

        // As long the player isn't game over.
        if (world.isGameOver()) return;

        // As long the player exists.
        // Draw player components.
        this.drawStatusEffects(renderer, player);
        this.drawScoreText(renderer, player, titleColor, valueColor);
        this.drawLevelText(renderer, player, titleColor, valueColor);
        this.drawSpeedText(renderer, player, titleColor, valueColor);
        this.drawLivesText(renderer, player, titleColor, valueColor);
    }

    /**
     * Draws the score information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the score from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(World, Renderer, Player)
     */
    public void drawScoreText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        renderer.drawTextCenter(this.infoFont, Language.translate("bubbleblaster.info.score"), 70, 30, titleColor);
        renderer.drawTextCenter(this.font, String.valueOf((int) player.getScore()), 70, 60, valueColor);
    }

    /**
     * Draws the level information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the level from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(World, Renderer, Player)
     */
    public void drawLevelText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        renderer.drawTextCenter(this.infoFont, Language.translate("bubbleblaster.info.level"), 140, 30, titleColor);
        renderer.drawTextCenter(this.font, String.valueOf(player.getLevel()), 140, 60, valueColor);
    }

    /**
     * Draws the score information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the score from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(World, Renderer, Player)
     */
    public void drawSpeedText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        renderer.drawTextCenter(this.infoFont, Language.translate("bubbleblaster.info.speed"), 210, 30, titleColor);
        renderer.drawTextCenter(this.font, String.valueOf(player.getSpeed()), 210, 60, valueColor);
    }

    /**
     * Draws the legacy health information for the player.
     *
     * @param renderer   renderer to draw with.
     * @param player     the player to get the level from.
     * @param titleColor the title color for the information.
     * @param valueColor the value color for the information.
     * @see #drawPlayerDetails(World, Renderer, Player)
     */
    public void drawLivesText(Renderer renderer, Player player, Color titleColor, Color valueColor) {
        // Level
        renderer.drawTextCenter(this.infoFont, Language.translate("bubbleblaster.info.lives"), 280, 30, titleColor);
        renderer.drawTextCenter(this.font, String.valueOf((int)(10 * player.getHealth() / player.getMaxHealth())), 280, 60, valueColor);
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

            int i = 0;
            for (StatusEffectInstance appliedEffect : player.getActiveEffects()) {
                // Renderer 2D
                int x = 320 + i * 196;
                int y = 16;
                renderer.scissored(x, y, 192, 38, () -> {
                    // Format duration to string.
                    String time = TimeUtils.formatDuration(appliedEffect.getRemainingTime());

                    // EffectInstance bar.
                    renderer.blit(x, y, 192, 38);

                    // EffectInstance icon.
                    renderer.setTexture(appliedEffect.getType().getIconId());
                    renderer.blit(x + 5, y + 3, 32, 32);
                    renderer.setColor(Color.rgba(255, 255, 255, 192));

                    renderer.drawTextLeft(this.font, time, 56, 19.5f, Color.WHITE.withAlpha(0xC0));
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
    public void drawTopBar(Renderer renderer, BubbleBlaster game) {
        Rectangle topBar = new Rectangle(0, 0, game.getWidth(), 70);
        renderer.fill(topBar, TOP_BAR_BG_COLOR);
        renderer.setColor(Color.rgb(0xadd8e6));
        renderer.setLineThickness(1);
        renderer.box(0, 71, game.getWidth(), 1, Color.rgb(0xadd8e6), new Insets(1));
    }

    /**
     * Sets Game Over flag
     * Yes, as the title says: it sets the game over flag in the HUD.
     */
    public void gameOver() {

        // Values
    }

    @Override
    public void onLevelUp(int newLevel) {
        this.showLevelUp = true;
        this.level = newLevel;
        this.hideLevelUpTime = System.currentTimeMillis() + 3000;
    }

    @Override
    public boolean renderBackground(Renderer renderer, Color origColorTop, Color origColorBottom) {
        renderer.fill(0, 0, this.width(), this.height(), BACKGROUND_COLOR);
        return true;
    }
}
