package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.libs.text.v1.TextObject;

import java.util.Random;

@Deprecated(forRemoval = true)
public class StartOptionsScreen extends Screen {
    private static final Color TEXT_COLOR = Color.WHITE.withAlpha(0xd0);
    private final Screen back;
    private final int seed;

    public StartOptionsScreen(Screen back) {
        this.back = back;
        this.seed = new Random().nextInt();
    }

    @Override
    public void init() {
        this.clearWidgets();

        this.add(Button.builder().bounds(this.width / 2 - 150, this.height / 2 + 5, 300, 40).text(TextObject.translation("bubbleblaster.screen.startOptions.start")).command(this::start).build());
    }

    private void start() {
        this.game.showScreen(new DifficultyScreen(this, this.seed));
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        super.render(game, renderer, mouseX, mouseY, deltaTime);

//        renderer.color(Theme.getMenuLabelColor()); // TODO: Add themes
        renderer.drawTextRight(Fonts.SANS_BIG.get(), "Seed: ", this.width / 2f - 120, this.height / 2f - 35, TEXT_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BIG.get(), String.valueOf(this.seed), this.width / 2f - 120, this.height / 2f - 35, TEXT_COLOR);
    }

    @Override
    public void back() {
        this.game.showScreen(this.back);
    }
}
