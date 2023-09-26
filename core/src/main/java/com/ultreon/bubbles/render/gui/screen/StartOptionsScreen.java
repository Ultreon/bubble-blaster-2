package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.bubbles.render.gui.widget.OptionsNumberInput;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.libs.text.v0.TextObject;

import java.util.Random;

public class StartOptionsScreen extends Screen {
    private final Screen back;
    private int seed;

    public StartOptionsScreen(Screen back) {
        this.back = back;
    }

    @Override
    public void init() {
        clearWidgets();

        seed = new Random().nextInt();
        OptionsNumberInput seedInput = add(new OptionsNumberInput(width / 2 - 150, height / 2 - 35, 300, 30, seed, Integer.MIN_VALUE, Integer.MAX_VALUE));
        seedInput.setResponder(text -> {
            try {
                seed = Integer.parseInt(text);
            } catch (Exception ignored) {
                {

                }
            }
        });
        add(new OptionsButton.Builder().bounds(width / 2 - 150, height / 2 + 5, 300, 30).text(TextObject.translation("bubbles/screen/start_options/start")).command(this::start).build());
    }

    private void start() {
        int value = seed;
        BubbleBlaster.getInstance().createGame(value, GameSettings.instance().getGamemode());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.setColor(0xff333333);
        renderer.fill(game.getBounds());

        super.render(game, renderer, mouseX, mouseY, deltaTime);

//        renderer.color(Theme.getMenuLabelColor()); // TODO: Add themes
        renderer.setColor(255, 255, 255, 96);
        renderer.drawRightAnchoredText(Fonts.SANS_REGULAR_20.get(), "Seed:", width / 2f - 120, height / 2f - 35);
    }

    public void back() {
        game.showScreen(back);
    }
}
