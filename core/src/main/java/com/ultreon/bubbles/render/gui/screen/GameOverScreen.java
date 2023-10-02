package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.libs.text.v1.TextObject;

import java.io.IOException;

public class GameOverScreen extends Screen {
    private static final Color GAME_OVER_COLOR_NORMAL = Color.argb(0x7fff3243);
    private static final Color GAME_OVER_COLOR_FLASH = Color.argb(0x7fff8432);
    private final boolean isHighScore;
    private final long score;
    private long gameOverTime;
    private final BitmapFont gameOverTitleFont = Fonts.DONGLE_60.get();
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
        this.make();

        this.gameOverTime = System.currentTimeMillis();

        this.add(new Button.Builder()
                .bounds((int) (BubbleBlaster.getMiddleX() - 128), 340, 256, 48)
                .text(TextObject.translation("bubbleblaster.screen.gameOver.backToTitle")).command(this::goToTitle).build());
    }

    private void goToTitle() {
        BubbleBlaster.getInstance().quitLoadedGame();
    }

    @Override
    public boolean close(Screen to) {
        this.dispose();

        return super.close(to);
    }

    @Override
    public void renderBackground(Renderer renderer) {
        super.renderBackground(renderer);

        if (this.isHighScore) {
            renderer.setColor(0xffffffff);
            renderer.drawTextCenter(this.gameOverTitleFont, "Congratulations!", this.width / 2f, 152);
            renderer.drawTextCenter(this.gameOverDescriptionFont, "You beat your high-score!", this.width / 2f, 216);
        } else {
            long cycled = (System.currentTimeMillis() - this.gameOverTime) % 4000;
            int phase = (int) (Math.floorDiv(cycled, 1000));
            switch (phase) {
                case 4, 3, 2 -> renderer.setColor(GAME_OVER_COLOR_NORMAL);
                case 1 -> renderer.setColor(GAME_OVER_COLOR_FLASH);
                case 0 -> MathHelper.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0);
            }
            renderer.setColor(MathHelper.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0));
            renderer.drawTextCenter(this.gameOverTitleFont, "Game Over", this.width / 2f, 152);
        }

        renderer.setColor(0x7fffffff);
        renderer.drawText(Fonts.SANS_REGULAR_20.get(), Long.toString(this.score), this.game.getScaledWidth() / 2f, 280, Anchor.CENTER);
    }

    public boolean isHighScore() {
        return this.isHighScore;
    }

    @Override
    public boolean doesPauseGame() {
        return false;
    }
}
