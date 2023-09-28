package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.libs.text.v0.TextObject;
import com.ultreon.libs.translations.v0.Language;

public class OutOfMemoryScreen extends Screen {
    public OutOfMemoryScreen() {
        super(null);

        if (this.game.isInGame() || this.game.environment != null) {
            this.game.crash(new Error(getClass().getSimpleName() + " name violation, should only be used when outside of the game."));
        }
    }

    @Override
    public void init() {
        this.clearWidgets();

        this.add(new OptionsButton.Builder()
                .bounds(width / 2 - 100, height / 3 + 140, 200, 40)
                .text(TextObject.translation("bubbleblaster/screen/out_of_memory/back_to_title"))
                .command(this::backToTitle)
                .build());
    }

    private void backToTitle() {
        this.game.showScreen(new TitleScreen());
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.drawCenteredText(Fonts.SANS_BOLD_48.get(), Language.translate("bubbleblaster/screen/out_of_memory/title"), width / 2f, height / 3f);
        renderer.drawCenteredText(Fonts.SANS_BOLD_14.get(), Language.translate("bubbleblaster/screen/out_of_memory/line1"), width / 2f, height / 3f + 60);
        renderer.drawCenteredText(Fonts.SANS_BOLD_14.get(), Language.translate("bubbleblaster/screen/out_of_memory/line2"), width / 2f, height / 3f + 80);
    }
}
