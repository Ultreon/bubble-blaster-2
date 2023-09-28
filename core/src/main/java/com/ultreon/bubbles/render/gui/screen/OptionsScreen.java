package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.libs.translations.v0.Language;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.bubbles.render.gui.widget.OptionsNumberInput;
import com.ultreon.bubbles.settings.GameSettings;

@SuppressWarnings("unused")
public class OptionsScreen extends Screen {
    private final OptionsNumberInput maxBubblesOption;
    private final OptionsButton languageButton;
    private final OptionsButton cancelButton;
    private final OptionsButton saveButton;
    private Screen back;

    public OptionsScreen(Screen back) {
        super();

        this.back = back;

        this.maxBubblesOption = new OptionsNumberInput(0, 0, 321, 48, GameSettings.instance().maxBubbles, 400, 2000);
        this.languageButton = new OptionsButton.Builder().bounds(0, 0, 321, 48).command(this::showLanguages).build();
        this.cancelButton = new OptionsButton.Builder().bounds(0, 0, 321, 48).command(this::back).build();
        this.saveButton = new OptionsButton.Builder().bounds(0, 0, 321, 48).command(this::save).build();
    }

    private void save() {
        int option = maxBubblesOption.getValue();

        GameSettings settings = GameSettings.instance();
        settings.maxBubbles = option;
        GameSettings.save();
    }

    private void showLanguages() {
        game.showScreen(new LanguageScreen(this));
    }

    private void back() {
        game.showScreen(back);
    }

    @Override
    public void init() {
        BubbleBlaster.getInstance().updateRPC();

        maxBubblesOption.make();
        languageButton.make();
        cancelButton.make();
        saveButton.make();
    }

    @Override
    public boolean onClose(Screen to) {
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
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
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

        cancelButton.setText(Language.translate("bubbleblaster/other/cancel"));
        languageButton.setText(Language.translate("bubbleblaster/screen/options/language"));
        saveButton.setText(Language.translate("bubbleblaster/other/save"));

        super.render(game, renderer, mouseX, mouseY, deltaTime);
    }

    public void renderBackground(BubbleBlaster game, Renderer renderer) {
        renderer.setColor(Color.rgb(0x606060));
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
    }
}
