package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.libs.text.v1.TextObject;

import java.io.IOException;
import java.time.Instant;

public class GameOverScreen extends Screen {
    private static final Color GAME_OVER_COLOR_NORMAL = Color.argb(0x7fff3243);
    private static final Color GAME_OVER_COLOR_FLASH = Color.argb(0x7fff8432);
    public static final TextObject TITLE = TextObject.translation("bubbleblaster.screen.gameOver");
    private static final TextObject HIGH_SCORE = TextObject.translation("bubbleblaster.screen.gameOver.highScore");
    private final boolean isHighScore;
    private final long score;
    private long gameOverTime;
    private final BitmapFont gameOverTitleFont = Fonts.DONGLE_60.get();
    private final BitmapFont gameOverDescriptionFont = Fonts.SANS_BOLD_14.get();

    public GameOverScreen(long score) {
        this(score, TITLE);
    }

    public GameOverScreen(long score, TextObject title) {
        super(title);
        this.score = score;

        GlobalSaveData globalData = GlobalSaveData.instance();

        this.isHighScore = globalData.getHighScore() < score;

        if (this.isHighScore) {
            globalData.updateHighScore(score, Instant.now());
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

        this.add(Button.builder()
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

        long cycled = (System.currentTimeMillis() - this.gameOverTime) % 4000;
        int phase = (int) (Math.floorDiv(cycled, 1000));
        switch (phase) {
            case 4:
            case 3:
            case 2:
                renderer.setColor(GAME_OVER_COLOR_NORMAL);
                break;
            case 1:
                renderer.setColor(GAME_OVER_COLOR_FLASH);
                break;
            case 0:
                MathHelper.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0);
                break;
        }
        renderer.drawTextCenter(this.gameOverTitleFont, this.title, this.width / 2f, 152, MathHelper.mixColors(GAME_OVER_COLOR_NORMAL, GAME_OVER_COLOR_FLASH, (double) cycled % 1000 / 1000.0));

        if (this.isHighScore)
            renderer.drawTextCenter(this.gameOverDescriptionFont, HIGH_SCORE, this.width / 2f, 216, Color.WHITE);

        renderer.drawTextCenter(Fonts.SANS_REGULAR_20.get(), Long.toString(this.score), this.game.getScaledWidth() / 2f, 280, Color.WHITE.withAlpha(0x80));
    }

    public boolean isHighScore() {
        return this.isHighScore;
    }

}
