package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.Input.Keys;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.input.GameInput;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.libs.text.v0.MutableText;
import com.ultreon.libs.text.v0.TextObject;

public class GamemodeScreen extends Screen {
    private static final TextObject TITLE = TextObject.translation("bubbles/screen/difficulty/title");
    private static final Gamemode[] GAMEMODES = {Gamemodes.MODERN.get(), Gamemodes.CLASSIC.get(), Gamemodes.LEGACY.get()};
    private final Difficulty difficulty;
    private final long seed;

    public GamemodeScreen(Screen backScreen, Difficulty difficulty, long seed) {
        super(TITLE, backScreen);
        this.difficulty = difficulty;
        this.seed = seed;
    }

    @Override
    public void init() {
        clearWidgets();

        int width = 200;
        int height = 60;
        int y = 150;
        int x = (this.width - width) / 2;
        for (Gamemode gamemode : GAMEMODES) {
            MutableText text = TextObject.translation(gamemode.getTranslationId());
            add(new OptionsButton.Builder()
                    .text(text)
                    .bounds(x, y, width, height)
                    .command(() -> this.play(difficulty, gamemode, this.seed))
                    .build());

            y += height + 2;
        }
    }

    public void play(Difficulty difficulty, Gamemode gamemode, long seed) {
        if (gamemode == Gamemodes.MODERN.get() && isKey()) {
            gamemode = Gamemodes.IMPOSSIBLE.get();
        }
        this.game.createGame(seed, gamemode, difficulty);
    }

    private boolean isKey() {
        return GameInput.areKeysDown(Keys.CONTROL_LEFT, Keys.SHIFT_LEFT);
    }
}
