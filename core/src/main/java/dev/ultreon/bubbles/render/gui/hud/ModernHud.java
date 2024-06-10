package dev.ultreon.bubbles.render.gui.hud;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import dev.ultreon.bubbles.GamePlatform;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.gamemode.Gamemode;
import dev.ultreon.bubbles.init.Fonts;
import dev.ultreon.bubbles.notification.Notification;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.util.RomanNumbers;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.libs.commons.v0.Mth;
import dev.ultreon.libs.text.v1.TextObject;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

import static dev.ultreon.bubbles.BubbleBlaster.id;
import static dev.ultreon.bubbles.BubbleBlasterConfig.SECS_BEFORE_RED_EFFECT_TIME;

/**
 * The classic hud, the hud that's almost identical to older versions and editions create the game.
 * For example the Python versions. The only thing changed is how the status effects are shown.
 *
 * @see HudType
 */
public class ModernHud extends HudType {

    // Colors
    private static final Color LEVEL_UP_COLOR = Color.argb(0x90ffffff);
    private boolean showLevelUp;
    private long hideLevelUpTime;
    private int level;
    private String levelUpText;
    private final GlyphLayout levelUpLayout = new GlyphLayout();
    private final BitmapFont levelUpFont = Fonts.SANS_HEADER_1.get();
    private final BitmapFont playerDetailsNameFont = Fonts.SANS_HEADER_3.get();
    private final BitmapFont playerDetailsInfoFont = Fonts.SANS_PARAGRAPH.get();
    private Instant gameOverTime;

    @Override
    public void renderHudOverlay(Renderer renderer, World world, Gamemode gamemode, float deltaTime) {
        var loadedGame = this.game.getLoadedGame();
        if (loadedGame == null) return;

        var player = gamemode.getPlayer();

        if (player == null) return;

        if (!world.isGameOver()) {
            this.game.profiler.section("Draw Badge", () -> this.drawBadge(renderer, player));
            this.game.profiler.section("Draw Status Effects", () -> this.drawStatusEffects(renderer, player));
        }

        this.game.profiler.section("Draw Messages", () -> this.drawMessages(renderer));
        this.game.profiler.section("Draw Level Up Message", () -> this.drawLevelUpMessage(renderer, gamemode));
        if (GamePlatform.get().isMobile()) {
            this.game.profiler.section("Draw Mobile Overlay", () -> this.drawMobileOverlay(renderer));
        }
    }

    private void drawMobileOverlay(Renderer renderer) {
        renderer.fill(this.game.mobileInput.getShootBtnRegion(), Color.BLACK.withAlpha(0x80));
        if (!this.game.hasScreenOpen()) {
            renderer.fill(this.game.mobileInput.getPauseBtnRegion(), Color.BLACK.withAlpha(0x80));
        }
    }

    private void drawBadge(Renderer renderer, Player player) {
        this.game.profiler.section("Draw Background", () -> this.drawBadgeBackground(renderer));
        this.game.profiler.section("Draw Player Details", () -> this.drawPlayerDetails(renderer, player));
    }

    private void drawBadgeBackground(Renderer renderer) {
        renderer.fill(20, 20, 400, 80, Color.BLACK.withAlpha(0x80));
    }

    /**
     * Draw the player details.
     *
     * @param renderer renderer to draw with.
     * @param player   the player to draw information for.
     */
    private void drawPlayerDetails(@NotNull Renderer renderer, @NotNull Player player) {
        var x = 20;
        var y = 20;

        var name = player.getName();
        renderer.setColor(0xffffffff);
        renderer.drawText(this.playerDetailsNameFont, name, x + 5, y + 5, Color.WHITE);
        renderer.drawText(this.playerDetailsInfoFont, TextObject.literal("Score: ").append(Math.round(player.getScore())).append("    Level: ").append(player.getLevel()), x + 5, y + 25, Color.WHITE.withAlpha(0xa0));
        renderer.drawText(this.playerDetailsInfoFont, TextObject.literal("Speed: ").append(Math.round(player.getCurrentSpeed())).append(" px/s"), x + 5, y + 40, Color.WHITE.withAlpha(0xa0));

        renderer.drawText(this.playerDetailsInfoFont, Integer.toString(player.getGoldCoins()), x + 325, y + 30, Color.WHITE.withAlpha(0xa0));
        renderer.drawText(this.playerDetailsInfoFont, Integer.toString(player.getSilverCoins()), x + 325, y + 60, Color.WHITE.withAlpha(0xa0));

        renderer.line(x + 5, y + 75, x + 295, y + 75, Color.WHITE.withAlpha(0x40));
        renderer.line(x + 300, y + 5, x + 300, y + 80, Color.WHITE.withAlpha(0x40));

        renderer.blit(this.game.getTextureManager().getOrLoadTexture(id("ui/coin_gold")), x + 305, y + 30, 16, 16);
        renderer.blit(this.game.getTextureManager().getOrLoadTexture(id("ui/coin_silver")), x + 305, y + 60, 16, 16);

        var hpText = TextObject.literal("HP: ").append((int) Math.floor(player.getHealth())).append(" / ").append((int) Math.floor(player.getMaxHealth()));

        var maxHealth = player.getMaxHealth();
        var health = Mth.clamp(player.getHealth(), 0, maxHealth);
        if (maxHealth != 0) {
            var ratio = health / maxHealth;

            Color color;
            if (ratio >= 0.5) color = Color.GREEN;
            else if (ratio >= 0.2) color = Color.GOLD;
            else color = Color.CRIMSON;

            renderer.drawText(this.playerDetailsInfoFont, hpText, x + 5, y + 60, ratio >= 0.2 ? Color.WHITE.withAlpha(0x50) : color);
            renderer.line(x + 5, y + 75, x + (int) (5 + 290 * health / maxHealth), y + 75, color);
        } else {
            renderer.drawText(this.playerDetailsInfoFont, hpText, x + 5, y + 60, Color.CRIMSON);
            renderer.line(x + 5, y + 75, x + (int) (5 + 290 * health / maxHealth), y + 75, Color.CRIMSON);
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

            renderer.fillRoundRect(
                    (float) (int) (gameBounds.getX() + gameBounds.getWidth() - width) / 2,
                    (float) (int) (gameBounds.getY() + gameBounds.getHeight() - height) / 2, (int) width, (int) height,
                    10, Color.BLACK.withAlpha(0x80));

            renderer.drawTextCenter(this.levelUpFont, text, gameBounds.getX() + gameBounds.getWidth() / 2, gameBounds.getY() + gameBounds.getHeight() / 2, LEVEL_UP_COLOR);
        }
    }

    /**
     * Draws the status effects. Showing icon, and time.
     *
     * @param renderer the renderer to draw with.
     * @param player   the player, to get the information about the status effects from.
     */
    private void drawStatusEffects(@NotNull Renderer renderer, @NotNull Player player) {
        var x = this.game.getWidth() - 320;
        var y = 20;

        for (var effectInstance : player.getActiveEffects()) {
            renderer.fill(x, y, 300, 50, Color.BLACK.withAlpha(0x80));

            // Format duration to string.
            var time = effectInstance.getRemainingTime().toSimpleString();

            try {
                renderer.blit(effectInstance.getType().getIcon(), x + 5, y + 5, 40, 40);
            } catch (Exception e) {
                this.game.notifications.notifyOnce(
                        UUID.fromString("ca1d5b52-1877-40fe-8c17-077dc637d9e2"),
                        Notification.builder("Broken Texture!", "The texture for a status effect is broken")
                                .subText("Rendering System")
                                .build()
                );

                renderer.fill(x + 5, y + 5, 40, 40, Color.WHITE.withAlpha(0x80));
            }

            var finalY = y;
            renderer.scissored(x + 50, y + 2, 248, 46, () -> {
                var translation = effectInstance.getType().getTranslation();
                translation.append(" " + RomanNumbers.toRoman(effectInstance.getStrength()));
                renderer.drawTextLeft(Fonts.SANS_PARAGRAPH_BOLD.get(), translation, x + 70, finalY + 15, Color.WHITE);

                var color = Color.WHITE.withAlpha(0x80);
                if (effectInstance.getRemainingTime().getSeconds() <= SECS_BEFORE_RED_EFFECT_TIME.get()) color = Color.rgb(0xff0000);
                renderer.drawTextLeft(Fonts.SANS_PARAGRAPH.get(), TextObject.literal(time), x + 70, finalY + 35, color);
            });

            y += 60;
        }
    }

    /**
     * Sets Game Over flag
     * Yes, as the title says: it sets the game over flag in the HUD.
     */
    public void gameOver() {
        // Values
        this.gameOverTime = Instant.now();
    }

    @Override
    public void onLevelUp(int newLevel) {
        this.showLevelUp = true;
        this.level = newLevel;
        this.hideLevelUpTime = System.currentTimeMillis() + 3000;
    }

    public Instant getGameOverTime() {
        return this.gameOverTime;
    }
}
