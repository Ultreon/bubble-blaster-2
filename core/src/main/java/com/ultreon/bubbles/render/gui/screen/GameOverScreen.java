package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
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
    private final BitmapFont gameOverTitleFont = Fonts.SANS_BOLD_60.get();
    private final BitmapFont gameOverDescriptionFont = Fonts.SANS_BOLD_14.get();

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
            renderer.setColor(0xffffffff);
            renderer.drawText(gameOverTitleFont, "Congratulations!", width / 2f, 152, Anchor.CENTER);
            renderer.drawText(gameOverDescriptionFont, "You beat your high-score!", width / 2f, 216, Anchor.CENTER);
        } else {
            long cycled = (System.currentTimeMillis() - gameOverTime) % 4000;
            int phase = (int) (Math.floorDiv(cycled, 1000));
            switch (phase) {
                case 4, 3, 2 -> renderer.setColor(GAME_OVER_COLOR_NORMAL);
                case 1 -> renderer.setColor(GAME_OVER_COLOR_FLASH);
                case 0 -> Mth.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0);
            }
            renderer.setColor(Mth.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0));
            renderer.drawText(gameOverTitleFont, "Game Over", width / 2f, 152, Anchor.CENTER);
        }

        renderer.setColor(0x7fffffff);
        renderer.drawText(Fonts.SANS_REGULAR_20.get(), Long.toString(score), game.getScaledWidth() / 2f, 280, Anchor.CENTER);
    }

    public boolean isHighScore() {
        return isHighScore;
    }

    @Override
    public boolean doesPauseGame() {
        return false;
    }
}
