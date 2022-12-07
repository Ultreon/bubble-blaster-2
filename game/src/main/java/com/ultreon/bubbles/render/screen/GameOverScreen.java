package com.ultreon.bubbles.render.screen;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.common.text.TranslationText;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.IngameButton;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.helpers.MathHelper;

import java.awt.*;
import java.io.IOException;

public class GameOverScreen extends Screen {
    private static final Color GAME_OVER_COLOR_NORMAL = new Color(0x7fff3243, true);
    private static final Color GAME_OVER_COLOR_FLASH = new Color(0x7fff8432, true);
    private final boolean isHighScore;
    private final Font titleFont = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD, 64);
    private final Font descriptionFont = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD, 14);
    private final Font scoreFont = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD, 32);
    private final long score;
    private long gameOverTime;

    public GameOverScreen(long score) {
        this.score = score;

        GlobalSaveData globalData = GlobalSaveData.instance();

        this.isHighScore = globalData.getHighScore() < score;

        if (this.isHighScore) {
            globalData.setHighScore(score, System.currentTimeMillis());
            try {
                globalData.dump();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void init() {
        make();

        gameOverTime = System.currentTimeMillis();

        add(new IngameButton.Builder()
                .bounds((int) (BubbleBlaster.getMiddleX() - 128), 340, 256, 48)
                .text(new TranslationText("bubbles/screen/game_over/back_to_title")).command(this::goToTitle).build());
    }

    private void goToTitle() {
        BubbleBlaster.getInstance().quitLoadedGame();
    }

    @Override
    public boolean onClose(Screen to) {
        destroy();

        return super.onClose(to);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        renderer.color(new Color(0, 0, 0, 128));
        renderer.rect(0, 0, game.getWidth(), game.getHeight());

        if (isHighScore) {
            renderer.color(0xffffffff);
            GraphicsUtils.drawCenteredString(renderer, "Congratulations!", new Rectangle(0, 120, game.getScaledWidth(), 64), titleFont);
            GraphicsUtils.drawCenteredString(renderer, "You beat your high-score!", new Rectangle(0, 184, game.getScaledWidth(), 64), descriptionFont);
        } else {
            long cycled = (System.currentTimeMillis() - gameOverTime) % 4000;
            int phase = (int) (Math.floorDiv(cycled, 1000));
            switch (phase) {
                case 4, 3, 2 -> renderer.color(GAME_OVER_COLOR_NORMAL);
                case 1 -> renderer.color(GAME_OVER_COLOR_FLASH);
                case 0 ->
                        MathHelper.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0);
            }
            renderer.color(MathHelper.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0));

            GraphicsUtils.drawCenteredString(renderer, "Game Over", new Rectangle(0, 120, game.getScaledWidth(), 64), titleFont);
        }

        renderer.color(0x7fffffff);
        GraphicsUtils.drawCenteredString(renderer, Long.toString(score), new Rectangle(0, 248, game.getScaledWidth(), 64), scoreFont);

        super.render(game, renderer, partialTicks);
    }

    public boolean isHighScore() {
        return isHighScore;
    }

    @Override
    public boolean doesPauseGame() {
        return false;
    }
}
