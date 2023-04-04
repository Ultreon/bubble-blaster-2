package com.ultreon.bubbles.render.screen;

import com.ultreon.bubbles.common.text.translation.Language;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.OptionsButton;
import com.ultreon.bubbles.render.gui.OptionsNumberInput;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.Util;

import java.awt.*;
import java.util.Objects;

@SuppressWarnings("unused")
public class OptionsScreen extends Screen {
    private static OptionsScreen INSTANCE;
    private final OptionsNumberInput maxBubblesOption;
    private final OptionsButton languageButton;
    private final OptionsButton cancelButton;
    private final OptionsButton saveButton;
    private Screen back;

    public OptionsScreen(Screen back) {
        super();

        OptionsScreen.INSTANCE = this;

        this.back = back;

        this.maxBubblesOption = new OptionsNumberInput(0, 0, 321, 48, GameSettings.instance().getMaxBubbles(), 400, 2000);
        this.languageButton = new OptionsButton.Builder().bounds(0, 0, 321, 48).command(this::showLanguages).build();
        this.cancelButton = new OptionsButton.Builder().bounds(0, 0, 321, 48).command(this::back).build();
        this.saveButton = new OptionsButton.Builder().bounds(0, 0, 321, 48).command(this::save).build();
    }

    public static OptionsScreen instance() {
        return INSTANCE;
    }

    private void save() {
        int maxBubbles = maxBubblesOption.getValue();

        GameSettings settings = GameSettings.instance();
        settings.setMaxBubbles(maxBubbles);
    }

    private void showLanguages() {
        Objects.requireNonNull(Util.getSceneManager()).displayScreen(new LanguageScreen(this));
    }

    private void back() {
        Objects.requireNonNull(Util.getSceneManager()).displayScreen(back);
    }

    @Override
    public void init() {
        BubbleBlaster.getInstance().updateRPC();

        BubbleBlaster.getEventBus().subscribe(this);

        maxBubblesOption.make();
        languageButton.make();
        cancelButton.make();
        saveButton.make();
    }

    @Override
    public boolean onClose(Screen to) {
        BubbleBlaster.getEventBus().unsubscribe(this);

        maxBubblesOption.destroy();
        languageButton.destroy();
        cancelButton.destroy();
        saveButton.destroy();

        if (to == back) {
            back = null;
        }
        return super.onClose(to);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        maxBubblesOption.setX((int) BubbleBlaster.getMiddleX() - 322);
        maxBubblesOption.setY((int) BubbleBlaster.getMiddleY() + 101);
        maxBubblesOption.setWidth(321);

        languageButton.setX((int) BubbleBlaster.getMiddleX() + 1);
        languageButton.setY((int) BubbleBlaster.getMiddleY() + 101);
        languageButton.setWidth(321);

        cancelButton.setX((int) BubbleBlaster.getMiddleX() - 322);
        cancelButton.setY((int) BubbleBlaster.getMiddleY() + 151);
        cancelButton.setWidth(321);

        saveButton.setX((int) BubbleBlaster.getMiddleX() + 1);
        saveButton.setY((int) BubbleBlaster.getMiddleY() + 151);
        saveButton.setWidth(321);

        renderBackground(game, renderer);

        cancelButton.setText(Language.translate("bubbles/other/cancel"));
        cancelButton.render(renderer);

        languageButton.setText(Language.translate("bubbles/screen/options/language"));
        languageButton.render(renderer);

        maxBubblesOption.render(renderer);

        saveButton.render(renderer);
        saveButton.setText(Language.translate("bubbles/other/save"));
    }

    public void renderBackground(BubbleBlaster game, Renderer renderer) {
        renderer.color(new Color(96, 96, 96));
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
    }
}