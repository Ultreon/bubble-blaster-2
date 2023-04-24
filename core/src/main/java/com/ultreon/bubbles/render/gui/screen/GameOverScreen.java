package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.render.gui.widget.IngameButton;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.libs.text.v0.TextObject;

import java.io.IOException;

public class GameOverScreen extends Screen {
    private static final Color GAME_OVER_COLOR_NORMAL = Color.argb(0x7fff3243);
    private static final Color GAME_OVER_COLOR_FLASH = Color.argb(0x7fff8432);
    private final boolean isHighScore;
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
                .text(TextObject.translation("bubbles/screen/game_over/back_to_title")).command(this::goToTitle).build());
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
    public void renderBackground(Renderer renderer) {
        super.renderBackground(renderer);

        if (isHighScore) {
            renderer.color(0xffffffff);
            font.draw(renderer, "Congratulations!", 64, width / 2f, 152, Thickness.BOLD, Anchor.CENTER);
            font.draw(renderer, "You beat your high-score!", 14, width / 2f, 216, Thickness.BOLD, Anchor.CENTER);
        } else {
            long cycled = (System.currentTimeMillis() - gameOverTime) % 4000;
            int phase = (int) (Math.floorDiv(cycled, 1000));
            switch (phase) {
                case 4, 3, 2 -> renderer.color(GAME_OVER_COLOR_NORMAL);
                case 1 -> renderer.color(GAME_OVER_COLOR_FLASH);
                case 0 -> Mth.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0);
            }
            renderer.color(Mth.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0));

            font.draw(renderer, "Game Over", 64, width / 2f, 152, Thickness.BOLD, Anchor.CENTER);
        }

        renderer.color(0x7fffffff);
        font.draw(renderer, Long.toString(score), 32, game.getScaledWidth() / 2f, 280, Thickness.BOLD, Anchor.CENTER);
    }

    public boolean isHighScore() {
        return isHighScore;
    }

    @Override
    public boolean doesPauseGame() {
        return false;
    }
}
