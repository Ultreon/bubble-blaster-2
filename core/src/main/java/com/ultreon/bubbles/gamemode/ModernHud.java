package com.ultreon.bubbles.gamemode;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.init.Fonts;
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
            game.profiler.section("Draw Badge", () -> renderer.subInstance(20, 20, 300, 80, subRender -> drawBadge(subRender, player)));
            game.profiler.section("Draw Status Effects", () -> renderer.subInstance(game.getWidth() - 320, 20, 300, game.getHeight() - 40, subRender -> drawStatusEffects(subRender, player)));
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
        renderer.rect(0, 0, 300, 80);
    }

    /**
     * Draw the player details.
     *
     * @param renderer renderer to draw with.
     * @param player   the player to draw information for.
     */
    private void drawPlayerDetails(@NotNull Renderer renderer, @NotNull Player player) {
        String name = player.getName();
        renderer.setColor(0xffffffff);
        renderer.drawText(playerDetailsNameFont, name, 5, 5, Anchor.NW);
        renderer.drawText(playerDetailsInfoFont, TextObject.literal("Score: ").append((int) player.getScore()), 5, 45, Anchor.NW);
        renderer.drawText(playerDetailsInfoFont, TextObject.literal("Level: ").append(player.getLevel()), 5, 60, Anchor.NW);

        renderer.setColor(0x50ffffff);
        double maxHealth = player.getMaxHealth();
        double health = Mth.clamp(player.getHealth(), 0, maxHealth);
        if (maxHealth != 0) {
            renderer.line(5, 75, 295, 75);

            double ratio = health / maxHealth;
            if (ratio >= 0.5) {
                renderer.setColor(0xff00ff00);
            } else if (ratio >= 0.2) {
                renderer.setColor(0xffffd000);
            } else {
                renderer.setColor(0xffff0000);
            }
            renderer.line(5, 75, (int) (5 + (290 * health / maxHealth)), 75);
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
            if (!levelUpText.equals(text)) {
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
        int y = 0;
        for (AppliedEffect appliedEffect : player.getActiveEffects()) {
            renderer.subInstance(0, y, 300, 50, effectRender -> {
                Identifier id = appliedEffect.getType().getId();

                effectRender.setColor(0x80000000);
                effectRender.rect(0, 0, 300, 50);

                // Format duration to string.
                String time = TimeUtils.formatDuration(appliedEffect.getRemainingTime());

                try {
                    effectRender.blit(appliedEffect.getType().getIcon(), 5, 5, 40, 40);
                } catch (Exception e) {
                    BubbleBlaster.getLogger().warn("Effect icon is broken:", e);

                    effectRender.setColor(0x80ffffff);
                    effectRender.rect(5, 5, 40, 40);
                }

                effectRender.setColor(0xffffffff);
                effectRender.drawText(Fonts.SANS_BOLD_20.get(), TextObject.translation(id.location() + "/status_effect/" + id.path() + "/name"), 50, 5, Anchor.NW);

                if (appliedEffect.getRemainingTime() < 2L) renderer.setColor(0xffff0000);
                effectRender.drawText(Fonts.SANS_BOLD_15.get(), TextObject.literal(time), 50, 45, Anchor.SW);
            });
            // Next
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
