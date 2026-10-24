package dev.ultreon.bubbles.render.gui.screen;

import dev.ultreon.bubbles.render.gui.widget.Button;
import dev.ultreon.libs.text.v1.TextObject;
import org.jetbrains.annotations.Nullable;

public class ConfirmScreen extends Screen {
    private final @Nullable Screen backScreen;
    private final Runnable callback;

    public ConfirmScreen(Runnable callback) {
        this.callback = callback;
        this.backScreen = this.game.getCurrentScreen();
    }

    @Override
    public void init() {
        this.add(Button.builder()
                .bounds(this.width / 2 - 50, this.height / 2 - 10, 100, 20)
                .text(TextObject.translation("gui.confirm"))
                .command(this.callback)
                .build()
        );
        this.add(Button.builder()
                .bounds(this.width / 2 - 50, this.height / 2 + 10, 100, 20)
                .text(TextObject.translation("gui.cancel"))
                .command(this::back)
                .build()
        );
    }

    @Override
    public void back() {
        this.game.showScreen(this.backScreen);
    }

    public @Nullable Screen getBackScreen() {
        return this.backScreen;
    }
}
