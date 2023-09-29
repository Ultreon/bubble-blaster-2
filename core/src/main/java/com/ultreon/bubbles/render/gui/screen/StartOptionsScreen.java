package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.bubbles.render.gui.widget.OptionsNumberInput;
import com.ultreon.libs.text.v0.TextObject;

import java.util.Random;

public class StartOptionsScreen extends Screen {
    private final Screen back;
    private int seed;

    public StartOptionsScreen(Screen back) {
        this.back = back;
        this.seed = new Random().nextInt();
    }

    @Override
    public void init() {
        clearWidgets();

//        OptionsNumberInput seedInput = add(new OptionsNumberInput(this.width / 2 - 150, this.height / 2 - 35, 300, 30, this.seed, Integer.MIN_VALUE, Integer.MAX_VALUE));
//        seedInput.setResponder(text -> {
//            try {
//                this.seed = Integer.parseInt(text);
//            } catch (Exception ignored) {
//
//            }
//        });
        add(new OptionsButton.Builder().bounds(this.width / 2 - 150, this.height / 2 + 5, 300, 40).text(TextObject.translation("bubbleblaster/screen/start_options/start")).command(this::start).build());
    }

    private void start() {
        this.game.showScreen(new DifficultyScreen(this, this.seed));
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        super.render(game, renderer, mouseX, mouseY, deltaTime);

//        renderer.color(Theme.getMenuLabelColor()); // TODO: Add themes
        renderer.setColor(Color.argb(0xd0ffffff));
        renderer.drawRightAnchoredText(Fonts.SANS_REGULAR_20.get(), "Seed: ", width / 2f - 120, height / 2f - 35);
        renderer.drawLeftAnchoredText(Fonts.SANS_REGULAR_20.get(), String.valueOf(this.seed), width / 2f - 120, height / 2f - 35);
    }

    public void back() {
        this.game.showScreen(this.back);
    }
}
