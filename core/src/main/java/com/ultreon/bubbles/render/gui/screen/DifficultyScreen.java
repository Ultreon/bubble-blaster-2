package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.libs.text.v0.MutableText;
import com.ultreon.libs.text.v0.TextObject;

public class DifficultyScreen extends Screen {
    private static final TextObject TITLE = TextObject.translation("bubbles/screen/difficulty/title");
    private final long seed;

    public DifficultyScreen(Screen backScreen, long seed) {
        super(TITLE, backScreen);
        this.seed = seed;
    }

    @Override
    public void init() {
        clearWidgets();

        int width = 150;
        int height = 40;
        int y = 150;
        int x = (this.width - width) / 2;
        for (Difficulty difficulty : Difficulty.values()) {
            MutableText text = difficulty.getTranslation();
            add(new OptionsButton.Builder()
                    .text(text)
                    .bounds(x, y, width, height)
                    .command(() -> this.next(difficulty, this.seed))
                    .build());

            y += height + 2;
        }
    }

    public void next(Difficulty difficulty, long seed) {
        this.game.showScreen(new GamemodeScreen(this, difficulty, seed));
    }
}
