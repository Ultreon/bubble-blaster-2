package com.ultreon.bubbles.gamemode;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.commons.util.TimeUtils;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.text.v0.TextObject;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Rectangle2D;
import java.time.Instant;
import java.util.UUID;

/**
 * The classic hud, the hud that's almost identical to older versions and editions create the game.
 * For example the Python versions. The only thing changed is how the status effects are shown.
 *
 * @see GameHud
 */
public class ModernHud extends GameHud {
    // Flags
    private boolean gameOver;

    // Colors
    private static final Color LEVEL_UP_COLOR = Color.argb(0x90ffffff);
    private boolean showLevelUp;
    private long hideLevelUpTime;
    private int level;
    private String levelUpText;
    private final GlyphLayout levelUpLayout = new GlyphLayout();
    private final BitmapFont levelUpFont = Fonts.SANS_REGULAR_14.get();
    private final BitmapFont playerDetailsNameFont = Fonts.SANS_BOLD_20.get();
    private final BitmapFont playerDetailsInfoFont = Fonts.SANS_REGULAR_12.get();
    private Instant gameOverTime;

    /**
     * Constructor create the hud, actually doesn't do much different from {@link GameHud}.
     *
     * @param gamemode game type bound to this hud.
     */
    public ModernHud(Gamemode gamemode) {
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
            game.profiler.section("Draw Badge", () -> drawBadge(renderer, player));
            game.profiler.section("Draw Status Effects", () -> drawStatusEffects(renderer, player));
        }

        game.profiler.section("Draw Messages", () -> drawMessages(renderer, game));

        game.profiler.section("Draw Level Up Message", () -> drawLevelUpMessage(renderer, gamemode));
    }

    private void drawBadge(Renderer renderer, Player player) {
        game.profiler.section("Draw Background", () -> drawBadgeBackground(renderer));
        game.profiler.section("Draw Player Details", () -> drawPlayerDetails(renderer, player));
    }

    private void drawBadgeBackground(Renderer renderer) {
        renderer.setColor(0x80000000);
        renderer.rect(20, 20, 300, 80);
    }

    /**
     * Draw the player details.
     *
     * @param renderer renderer to draw with.
     * @param player   the player to draw information for.
     */
    private void drawPlayerDetails(@NotNull Renderer renderer, @NotNull Player player) {
        int x = 20;
        int y = 20;

        String name = player.getName();
        renderer.setColor(0xffffffff);
        renderer.drawText(playerDetailsNameFont, name, x + 5, y + 5, Anchor.NW);
        renderer.drawRightAnchoredText(playerDetailsInfoFont, TextObject.literal("Score: ").append((int) player.getScore()).getText(), x + 5, y + 45);
        renderer.drawRightAnchoredText(playerDetailsInfoFont, TextObject.literal("Level: ").append(player.getLevel()).getText(), x + 5, y + 60);

        renderer.setColor(0x50ffffff);
        double maxHealth = player.getMaxHealth();
        double health = Mth.clamp(player.getHealth(), 0, maxHealth);
        if (maxHealth != 0) {
            renderer.line(x + 5, y + 75, x + 295, y + 75);

            double ratio = health / maxHealth;
            if (ratio >= 0.5) {
                renderer.setColor(0xff00ff00);
            } else if (ratio >= 0.2) {
                renderer.setColor(0xffffd000);
            } else {
                renderer.setColor(0xffff0000);
            }
            renderer.line(x + 5, y + 75, x + (int) (5 + (290 * health / maxHealth)), y + 75);
        }
    }

    private void drawMessages(Renderer renderer, BubbleBlaster game) {
        LoadedGame loadedGame = game.getLoadedGame();
        if (loadedGame != null) {
            loadedGame.renderHUD(game, renderer);
        }
    }

    /**
     * Draw the game over message.
     *
     * @param renderer the renderer to draw with.
     * @param gamemode the game type bound to this hud.
     */
    private void drawLevelUpMessage(Renderer renderer, Gamemode gamemode) {
        renderer.setColor(LEVEL_UP_COLOR);

        if (showLevelUp && System.currentTimeMillis() > hideLevelUpTime) {
            showLevelUp = false;
            return;
        }

        // Level up message
        if (showLevelUp) {
            String text = "Level " + level;
            if (!text.equals(this.levelUpText)) {
                levelUpText = text;
                levelUpLayout.setText(levelUpFont, text);
            }

            float textWidth = levelUpLayout.width;
            float textHeight = levelUpLayout.height;

            float width = textWidth + 16;
            float height = textHeight + 16;

            Rectangle2D gameBounds = gamemode.getGameBounds();

            renderer.setColor(Color.argb(0x7f000000));

            renderer.roundRect(
                    (int)(gameBounds.getX() + gameBounds.getWidth() - width) / 2,
                    (int)(gameBounds.getY() + gameBounds.getHeight() - height) / 2, (int) width, (int) height,
                    10, 10);

            renderer.setColor(LEVEL_UP_COLOR);

            renderer.drawCenteredText(levelUpFont, text, (float) (gameBounds.getX() + gameBounds.getWidth() / 2), (float) (gameBounds.getY() + gameBounds.getHeight() / 2));
        }
    }

    /**
     * Draws the status effects. Showing icon, and time.
     *
     * @param renderer the renderer to draw with.
     * @param player   the player, to get the information about the status effects from.
     */
    private void drawStatusEffects(@NotNull Renderer renderer, @NotNull Player player) {
        int x = game.getWidth() - 320;
        int y = 20;

        for (StatusEffectInstance appliedEffect : player.getActiveEffects()) {
            Identifier id = appliedEffect.getType().getId();

            renderer.setColor(0x80000000);
            renderer.rect(x, y, 300, 50);

            // Format duration to string.
            String time = TimeUtils.formatDuration(appliedEffect.getRemainingTime());

            try {
                renderer.blit(appliedEffect.getType().getIcon(), x + 5, y + 5, 40, 40);
            } catch (Exception e) {
                this.game.notifications.notifyOnce(UUID.fromString("ca1d5b52-1877-40fe-8c17-077dc637d9e2"), new Notification("Broken Texture!", "The texture for a status effect is broken", "Rendering System"));

                renderer.setColor(0x80ffffff);
                renderer.rect(x + 5, y + 5, 40, 40);
            }

            renderer.setColor(0xffffffff);
            renderer.drawLeftAnchoredText(Fonts.SANS_BOLD_20.get(), TextObject.translation(id.location() + "/status_effect/" + id.path() + "/name").getText(), x + 70, y + 15);

            if (appliedEffect.getRemainingTime().toSeconds() <= BubbleBlasterConfig.SECS_BEFORE_RED_EFFECT_TIME.get()) renderer.setColor(Color.rgb(0xff0000));

            renderer.drawLeftAnchoredText(Fonts.SANS_REGULAR_16.get(), TextObject.literal(time).getText(), x + 70, y + 40);

            y += 60;
        }
    }

    /**
     * Sets Game Over flag
     * Yes, as the title says: it sets the game over flag in the HUD.
     */
    public void setGameOver() {
        gameOver = true;
        // Values
        gameOverTime = Instant.now();
    }

    @Override
    public void onLevelUp(int to) {
        this.showLevelUp = true;
        this.level = to;
        this.hideLevelUpTime = System.currentTimeMillis() + 3000;
    }

    public Instant getGameOverTime() {
        return gameOverTime;
    }
}
