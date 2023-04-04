package com.ultreon.bubbles.render.screen;

import com.ultreon.bubbles.common.text.TranslationText;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.OptionsButton;
import com.ultreon.bubbles.render.gui.OptionsNumberInput;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.GraphicsUtils;

import java.awt.*;
import java.util.Random;

public class StartOptionsScreen extends Screen {
    private final Screen back;
    private OptionsNumberInput seedInput;
    private OptionsButton startBtn;
    private int seed;

    public StartOptionsScreen(Screen back) {
        this.back = back;
    }

    @Override
    public void init() {
        clearWidgets();

        seed = new Random().nextInt();
        seedInput = add(new OptionsNumberInput(width / 2 - 150, height / 2 - 35, 300, 30, seed, Integer.MIN_VALUE, Integer.MAX_VALUE));
        seedInput.setResponder(text -> {
            try {
                seed = Integer.parseInt(text);
            } catch (Exception ignored) {
                {

                }
            }
        });
        startBtn = add(new OptionsButton.Builder().bounds(width / 2 - 150, height / 2 + 5, 300, 30).text(new TranslationText("bubbles/screen/start_options/start")).command(this::start).build());
    }

    private void start() {
        int value = seed;
        BubbleBlaster.getInstance().createGame(value, GameSettings.instance().getGamemode());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        renderer.color(0xff333333);
        renderer.fill(game.getBounds());

        super.render(game, renderer, partialTicks);

//        renderer.color(Theme.getMenuLabelColor()); // TODO: Add themes
        renderer.color(255, 255, 255, 96);
        GraphicsUtils.drawRightAnchoredString(renderer, "Seed:", new Point(width / 2 - 120, height / 2 - 35), 30, new Font("Helvetica", Font.PLAIN, 20));
    }

    public void back() {
        game.showScreen(back);
    }
}