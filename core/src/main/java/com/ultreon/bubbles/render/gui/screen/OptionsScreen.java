package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.render.gui.widget.OptionsNumberInput;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.text.Translations;
import com.ultreon.libs.translations.v1.Language;

@SuppressWarnings("unused")
public class OptionsScreen extends Screen {
    private OptionsNumberInput maxBubblesOption;
    private Button languageButton;
    private Button cancelButton;
    private Button saveButton;
    private final Screen back;

    public OptionsScreen(Screen back) {
        super();

        this.back = back;
    }

    private void save() {
        int option = this.maxBubblesOption.getValue();

        GameSettings settings = GameSettings.instance();
        settings.maxBubbles = option;
        GameSettings.save();
    }

    private void showLanguages() {
        this.game.showScreen(new LanguageScreen(this));
    }

    private void back() {
        this.game.showScreen(this.back);
    }

    @Override
    public void init() {
        BubbleBlaster.getInstance().updateRPC();

        this.maxBubblesOption = this.add(new OptionsNumberInput((int) BubbleBlaster.getMiddleX() - 322, (int) BubbleBlaster.getMiddleY() + 101, 321, 48, GameSettings.instance().maxBubbles, 400, 2000));
        this.languageButton = this.add(Button.builder().bounds((int) BubbleBlaster.getMiddleX() + 1, (int) BubbleBlaster.getMiddleY() + 101, 321, 48).command(this::showLanguages).build());
        this.cancelButton = this.add(Button.builder().bounds((int) BubbleBlaster.getMiddleX() - 322, (int) BubbleBlaster.getMiddleY() + 151, 321, 48).command(this::back).build());
        this.saveButton = this.add(Button.builder().bounds((int) BubbleBlaster.getMiddleX() + 1, (int) BubbleBlaster.getMiddleY() + 151, 321, 48).command(this::save).build());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.maxBubblesOption.setPos((int) BubbleBlaster.getMiddleX() - 322, (int) BubbleBlaster.getMiddleY() + 101);
        this.maxBubblesOption.setWidth(321);

        this.languageButton.setPos((int) BubbleBlaster.getMiddleX() + 1, (int) BubbleBlaster.getMiddleY() + 101);
        this.languageButton.setWidth(321);

        this.cancelButton.setPos((int) BubbleBlaster.getMiddleX() - 322, (int) BubbleBlaster.getMiddleY() + 151);
        this.cancelButton.setWidth(321);

        this.saveButton.setPos((int) BubbleBlaster.getMiddleX() + 1, (int) BubbleBlaster.getMiddleY() + 151);
        this.saveButton.setWidth(321);

        this.cancelButton.setText(Translations.CANCEL);
        this.languageButton.setText(Language.translate("bubbleblaster.screen.options.language"));
        this.saveButton.setText(Translations.SAVE);

        super.render(game, renderer, mouseX, mouseY, deltaTime);
    }

    public void renderBackground(BubbleBlaster game, Renderer renderer) {
        renderer.fill(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight(), Color.GRAY_6);
    }
}
