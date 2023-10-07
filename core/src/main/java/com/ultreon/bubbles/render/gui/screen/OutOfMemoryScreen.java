package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.libs.text.v1.TextObject;
import com.ultreon.libs.translations.v1.Language;

public final class OutOfMemoryScreen extends Screen {
    public OutOfMemoryScreen() {
        super(TextObject.EMPTY);

        if (this.game.isInGame() || this.game.world != null) {
            BubbleBlaster.crash(new Error(this.getClass().getSimpleName() + " name violation, should only be used when outside of the game."));
        }
    }

    @Override
    public void init() {
        this.clearWidgets();

        this.add(Button.builder()
                .bounds(this.width / 2 - 100, this.height / 3 + 140, 200, 40)
                .text(TextObject.translation("bubbleblaster.screen.outOfMemory.backToTitle"))
                .command(this::backToTitle)
                .build());
    }

    private void backToTitle() {
        this.game.showScreen(new TitleScreen());
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.drawTextCenter(Fonts.SANS_TITLE.get(), Language.translate("bubbleblaster.screen.outOfMemory.title"), this.width / 2f, this.height / 3f, Color.WHITE);
        renderer.drawTextCenter(Fonts.SANS_PARAGRAPH_BOLD.get(), Language.translate("bubbleblaster.screen.outOfMemory.line1"), this.width / 2f, this.height / 3f + 60, Color.WHITE);
        renderer.drawTextCenter(Fonts.SANS_PARAGRAPH_BOLD.get(), Language.translate("bubbleblaster.screen.outOfMemory.line2"), this.width / 2f, this.height / 3f + 80, Color.WHITE);
    }
}
