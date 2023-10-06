package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.render.gui.widget.NumberSlider;
import com.ultreon.bubbles.text.Translations;
import com.ultreon.libs.text.v1.TextObject;

import static com.ultreon.bubbles.BubbleBlasterConfig.AUTO_SAVE_RATE;
import static com.ultreon.bubbles.BubbleBlasterConfig.MAX_FRAMERATE;

public class GenericOptionsScreen extends Screen {
    private NumberSlider maxBubblesOption;
    private Button languageButton;
    private Button cancelButton;
    private Button saveButton;
    private final Screen back;
    private NumberSlider autoSaveRate;
    private NumberSlider maxFramerate;

    public GenericOptionsScreen(Screen back) {
        super();

        this.back = back;
    }

    private void save() {
        AUTO_SAVE_RATE.set(this.autoSaveRate.getValue());
        MAX_FRAMERATE.set(this.maxFramerate.getValue());
        BubbleBlasterConfig.save();
    }

    private void back() {
        this.game.showScreen(this.back);
    }

    @Override
    public void init() {
        this.autoSaveRate = this.add(NumberSlider.builder()
                .value(AUTO_SAVE_RATE)
                .label(TextObject.translation("bubbleblaster.screen.options.generic.autoSaveRate"))
                .bounds(this.middleX - 301, this.middleY + 101, 300, 48)
                .entryWidth(80).build());
        this.maxFramerate = this.add(NumberSlider.builder()
                .value(MAX_FRAMERATE)
                .label(TextObject.translation("bubbleblaster.screen.options.generic.maxFramerate"))
                .bounds(this.middleX + 1, this.middleY + 101, 300, 48)
                .entryWidth(80).build());
        this.cancelButton = this.add(Button.builder()
                .text(Translations.CANCEL)
                .bounds(this.middleX - 151, this.middleY + 151, 150, 48)
                .command(this::back).build());
        this.saveButton = this.add(Button.builder()
                .text(Translations.SAVE)
                .bounds(this.middleX + 1, this.middleY + 151, 150, 48)
                .command(this::save).build());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        super.render(game, renderer, mouseX, mouseY, deltaTime);
    }

    public void renderBackground(BubbleBlaster game, Renderer renderer) {
        renderer.fill(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight(), Color.GRAY_6);
    }
}
