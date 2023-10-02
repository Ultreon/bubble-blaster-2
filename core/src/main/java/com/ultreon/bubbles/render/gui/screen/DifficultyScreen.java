package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.libs.text.v1.MutableText;
import com.ultreon.libs.text.v1.TextObject;

import java.math.BigDecimal;

public class DifficultyScreen extends Screen {
    private static final TextObject TITLE = TextObject.translation("bubbles/screen/difficulty/title");
    private final long seed;

    public DifficultyScreen(Screen backScreen, long seed) {
        super(TITLE, backScreen);
        this.seed = seed;
    }

    @Override
    public void init() {
        this.clearWidgets();

        Difficulty[] values = Difficulty.values();
        int gap = 2;
        int width = 300;
        int height = 50;
        int totalHeight = (height + gap) * values.length - gap;
        int y = (this.height - totalHeight) / 2;
        int x = (this.width - width) / 2;
        for (Difficulty difficulty : values) {
            MutableText text = difficulty.getTranslation();
            this.add(Button.builder()
                    .text(text.append(" (" + DifficultyScreen.fullNumberString(difficulty.getPlainModifier()) + "x)"))
                    .bounds(x, y, width, height)
                    .command(() -> this.next(difficulty, this.seed))
                    .build());

            y += height + gap;
        }
    }

    public static String fullNumberString(double value) {
        return BigDecimal.valueOf(value).toPlainString();
    }

    public static String fullNumberString(float value) {
        return new BigDecimal(Float.toString(value)).toPlainString();
    }

    public void next(Difficulty difficulty, long seed) {
        this.game.showScreen(new GamemodeScreen(this, difficulty, seed));
    }
}
