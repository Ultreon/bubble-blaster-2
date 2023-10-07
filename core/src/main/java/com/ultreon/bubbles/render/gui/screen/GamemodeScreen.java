package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.Input.Keys;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.input.DesktopInput;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.libs.text.v1.TextObject;

@Deprecated(forRemoval = true)
public class GamemodeScreen extends Screen {
    private static final TextObject TITLE = TextObject.translation("bubbles/screen/difficulty/title");
    private static final Gamemode[] GAMEMODES = {Gamemodes.NORMAL.get(), Gamemodes.TIMED.get()};
    private final Difficulty difficulty;
    private final long seed;

    public GamemodeScreen(Screen backScreen, Difficulty difficulty, long seed) {
        super(TITLE, backScreen);
        this.difficulty = difficulty;
        this.seed = seed;
    }

    @Override
    public void init() {
        this.clearWidgets();

        var width = 200;
        var height = 60;
        var y = 150;
        var x = (this.width - width) / 2;
        for (var gamemode : GAMEMODES) {
            var text = TextObject.translation(gamemode.getTranslationId());
            this.add(Button.builder()
                    .text(text)
                    .bounds(x, y, width, height)
                    .command(() -> this.play(this.difficulty, gamemode, this.seed))
                    .build());

            y += height + 2;
        }
    }

    public void play(Difficulty difficulty, Gamemode gamemode, long seed) {
        if (gamemode == Gamemodes.NORMAL.get() && this.isKey()) {
            gamemode = Gamemodes.IMPOSSIBLE.get();
        }
        this.game.createGame(seed, gamemode, difficulty);
    }

    private boolean isKey() {
        return DesktopInput.areKeysDown(Keys.CONTROL_LEFT, Keys.SHIFT_LEFT);
    }
}
