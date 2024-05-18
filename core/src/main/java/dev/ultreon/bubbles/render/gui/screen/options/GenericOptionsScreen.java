package dev.ultreon.bubbles.render.gui.screen.options;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.BubbleBlasterConfig;
import dev.ultreon.bubbles.input.MobileInput;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.bubbles.render.gui.widget.Button;
import dev.ultreon.bubbles.render.gui.widget.NumberSlider;
import dev.ultreon.bubbles.render.gui.widget.ToggleButton;
import dev.ultreon.bubbles.text.Translations;
import dev.ultreon.libs.text.v1.TextObject;

import static dev.ultreon.bubbles.BubbleBlasterConfig.*;

public class GenericOptionsScreen extends Screen {
    private ToggleButton enableTouchPressure;
    private ToggleButton enableAnnoyingEasterEggs;
    private ToggleButton enableEasterEggs;
    private NumberSlider autoSaveRate;
    private NumberSlider maxFramerate;
    private Button cancelButton;
    private Button saveButton;

    public GenericOptionsScreen(Screen back) {
        super(back);
    }

    public void save() {
        ENABLE_TOUCH_PRESSURE.set(this.enableTouchPressure.isToggled() && this.enableTouchPressure.enabled);
        ENABLE_ANNOYING_EASTER_EGGS.set(this.enableAnnoyingEasterEggs.isToggled());
        ENABLE_EASTER_EGGS.set(this.enableEasterEggs.isToggled());
        AUTO_SAVE_RATE.set(this.autoSaveRate.getValue());
        MAX_FRAMERATE.set(this.maxFramerate.getValue());
        BubbleBlasterConfig.save();
        this.back();
    }

    @Override
    public void init() {
        var entryWidth = 100;
        this.enableTouchPressure = this.add(ToggleButton.builder()
                .toggled(ENABLE_TOUCH_PRESSURE)
                .text(TextObject.translation("bubbleblaster.screen.options.generic.touchPressure"))
                .bounds(this.middleX + 1, this.middleY + 1, 300, 48).build());
        this.enableTouchPressure.enabled = MobileInput.isPressureAvailable();
        this.enableAnnoyingEasterEggs = this.add(ToggleButton.builder()
                .toggled(ENABLE_ANNOYING_EASTER_EGGS)
                .text(TextObject.translation("bubbleblaster.screen.options.generic.enableAnnoyingEasterEggs"))
                .bounds(this.middleX - 301, this.middleY + 51, 300, 48).build());
        this.enableEasterEggs = this.add(ToggleButton.builder()
                .toggled(ENABLE_EASTER_EGGS)
                .text(TextObject.translation("bubbleblaster.screen.options.generic.enableEasterEggs"))
                .bounds(this.middleX + 1, this.middleY + 51, 300, 48).build());
        this.autoSaveRate = this.add(NumberSlider.builder()
                .value(AUTO_SAVE_RATE)
                .label(TextObject.translation("bubbleblaster.screen.options.generic.autoSaveRate"))
                .bounds(this.middleX - 301, this.middleY + 101, 300, 48)
                .entryWidth(entryWidth).build());
        this.maxFramerate = this.add(NumberSlider.builder()
                .value(MAX_FRAMERATE)
                .label(TextObject.translation("bubbleblaster.screen.options.generic.maxFramerate"))
                .bounds(this.middleX + 1, this.middleY + 101, 300, 48)
                .entryWidth(entryWidth).build());
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

    public Button getCancelButton() {
        return this.cancelButton;
    }

    public Button getSaveButton() {
        return this.saveButton;
    }
}
