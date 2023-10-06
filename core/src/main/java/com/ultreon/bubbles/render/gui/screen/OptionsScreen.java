package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.text.Translations;
import com.ultreon.libs.text.v1.TextObject;

public class OptionsScreen extends Screen {
    private Button gameplayButton;
    private Button languageButton;
    private Button cancelButton;
    private Button saveButton;
    private final Screen back;

    public OptionsScreen(Screen back) {
        super();

        this.back = back;
    }

    private void save() {

    }

    private void showGameplay() {
        this.game.showScreen(new GameplayOptionsScreen(this));
    }

    private void showLanguages() {
        this.game.showScreen(new LanguageScreen(this));
    }

    private void back() {
        this.game.showScreen(this.back);
    }

    @Override
    public void init() {
        this.gameplayButton = this.add(Button.builder().text(TextObject.translation("bubbleblaster.screen.options.gameplay")).bounds(this.middleX - 201, this.middleY + 101, 200, 48).command(this::showGameplay).build());
        this.languageButton = this.add(Button.builder().text(TextObject.translation("bubbleblaster.screen.options.language")).bounds(this.middleX + 1, this.middleY + 101, 200, 48).command(this::showLanguages).build());
        this.cancelButton = this.add(Button.builder().text(Translations.CANCEL).bounds(this.middleX - 151, this.middleY + 151, 150, 48).command(this::back).build());
        this.saveButton = this.add(Button.builder().text(Translations.SAVE).bounds(this.middleX + 1, this.middleY + 151, 150, 48).command(this::save).build());
    }

    public void renderBackground(BubbleBlaster game, Renderer renderer) {
        renderer.fill(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight(), Color.GRAY_6);
    }
}
