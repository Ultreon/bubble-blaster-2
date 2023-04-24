package com.ultreon.bubbles.gamemode;

import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.commons.util.TimeUtils;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.text.v0.TextObject;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

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
            game.profiler.section("Draw Badge", () -> drawBadge(renderer.subInstance(20, 20, 300, 80), player));
            game.profiler.section("Draw Status Effects", () -> drawStatusEffects(renderer.subInstance(game.getWidth() - 320, 20, 300, game.getHeight() - 40), player));
        }

        game.profiler.section("Draw Messages", () -> drawMessages(renderer, game));

        game.profiler.section("Draw Level Up Message", () -> drawLevelUpMessage(renderer, gamemode));
    }

    private void drawBadge(Renderer renderer, Player player) {
        game.profiler.section("Draw Background", () -> drawBadgeBackground(renderer));
        game.profiler.section("Draw Player Details", () -> drawPlayerDetails(renderer, player));
    }

    private void drawBadgeBackground(Renderer renderer) {
        renderer.color(0x80000000);
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
        renderer.color(0xffffffff);
        font.draw(renderer, name, 20, 5, 5, Thickness.BOLD, Anchor.NW);
        font.draw(renderer, TextObject.literal("Score: ").append((int) player.getScore()), 12, 5, 45, Thickness.BOLD, Anchor.NW);
        font.draw(renderer, TextObject.literal("Level: ").append(player.getLevel()), 12, 5, 60, Thickness.BOLD, Anchor.NW);

        renderer.color(0x50ffffff);
        double maxHealth = player.getMaxHealth();
        double health = Mth.clamp(player.getHealth(), 0, maxHealth);
        if (maxHealth != 0) {
            renderer.line(5, 75, 295, 75);

            double ratio = health / maxHealth;
            if (ratio >= 0.5) {
                renderer.color(0xff00ff00);
            } else if (ratio >= 0.2) {
                renderer.color(0xffffd000);
            } else {
                renderer.color(0xffff0000);
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

            renderer.roundRect(
                    (int)(gameBounds.getX() + gameBounds.getWidth() - width) / 2,
                    (int)(gameBounds.getY() + gameBounds.getHeight() - height) / 2, width, height,
                    10, 10);

            renderer.color(LEVEL_UP_COLOR);
            font.draw(renderer, text, 50, (float) (gameBounds.getX() + gameBounds.getWidth() / 2), (float) (gameBounds.getY() + gameBounds.getHeight() / 2), Thickness.BOLD, Anchor.S);
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
            // GraphicsProcessor 2D
            Renderer effectRender = renderer.subInstance(0, y, 300, 50);
            Identifier id = appliedEffect.getType().getId();
            
            effectRender.color(0x80000000);
            effectRender.rect(0, 0, 300, 50);

            // Format duration to string.
            String time = TimeUtils.formatDuration(appliedEffect.getRemainingTime());

            try {
                appliedEffect.getType().getIcon().draw(effectRender, 5, 5, 40, 40);
            } catch (Exception e) {
                e.printStackTrace();
                effectRender.color(0x80ffffff);
                effectRender.rect(5, 5, 40, 40);
            }

            effectRender.color(0xffffffff);
            font.draw(effectRender, TextObject.translation(id.location() + "/status_effect/" + id.path() + "/name"), 20, 50, 5, Thickness.BOLD, Anchor.NW);

            if (appliedEffect.getRemainingTime() < 2L) renderer.color(0xffff0000);
            font.draw(effectRender, TextObject.literal(time), 15, 50, 45, Thickness.BOLD, Anchor.SW);

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
        long gameOverTime = System.currentTimeMillis();
    }

    @Override
    public void onLevelUp(int to) {
        this.showLevelUp = true;
        this.level = to;
        this.hideLevelUpTime = System.currentTimeMillis() + 3000;
    }
}
