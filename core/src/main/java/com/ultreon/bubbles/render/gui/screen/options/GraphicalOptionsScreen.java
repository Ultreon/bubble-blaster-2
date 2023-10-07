package com.ultreon.bubbles.render.gui.screen.options;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.render.gui.widget.DecimalNumberInput;
import com.ultreon.bubbles.render.gui.widget.NumberSlider;
import com.ultreon.bubbles.render.gui.widget.TextEntry;
import com.ultreon.bubbles.text.Translations;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.exception.RegistryException;
import com.ultreon.libs.text.v1.TextObject;

import static com.ultreon.bubbles.BubbleBlasterConfig.*;

public class GraphicalOptionsScreen extends Screen {
    private DecimalNumberInput bubbleLineThickness;
    private DecimalNumberInput defaultEffectSpeed;
    private NumberSlider secsBeforeRedEffectTime;
    private TextEntry gameHud;
    private Button cancelButton;
    private Button saveButton;

    public GraphicalOptionsScreen(Screen back) {
        super(back);
    }

    public void save() {
        BUBBLE_LINE_THICKNESS.set(this.bubbleLineThickness.getValueFloat());
        DEFAULT_EFFECT_SPEED.set(this.defaultEffectSpeed.getValueFloat());
        SECS_BEFORE_RED_EFFECT_TIME.set(this.secsBeforeRedEffectTime.getValue());
        var validText = this.gameHud.getValidText();
        if (validText != null) GAME_HUD.set(validText);
        BubbleBlasterConfig.save();
        this.back();
    }

    @Override
    public void init() {
        var entryWidth = 100;
        this.bubbleLineThickness = this.add(DecimalNumberInput.builder()
                .value(BUBBLE_LINE_THICKNESS)
                .label(TextObject.translation("bubbleblaster.screen.options.graphical.bubbleLineThickness"))
                .bounds(this.middleX - 301, this.middleY + 51, 300, 48)
                .entryWidth(entryWidth)
                .build());
        this.defaultEffectSpeed = this.add(DecimalNumberInput.builder()
                .value(DEFAULT_EFFECT_SPEED)
                .label(TextObject.translation("bubbleblaster.screen.options.graphical.defaultEffectSpeed"))
                .bounds(this.middleX + 1, this.middleY + 51, 300, 48)
                .entryWidth(entryWidth).build());
        this.secsBeforeRedEffectTime = this.add(NumberSlider.builder()
                .value(SECS_BEFORE_RED_EFFECT_TIME)
                .label(TextObject.translation("bubbleblaster.screen.options.graphical.secsBeforeRedEffectTime"))
                .bounds(this.middleX - 301, this.middleY + 101, 300, 48)
                .entryWidth(entryWidth).build());
        this.gameHud = this.add(TextEntry.builder()
                .text(GAME_HUD)
                .responder(s -> {
                    var id = Identifier.tryParse(s);
                    if (id == null) return false;

                    try {
                        return Registries.HUD.getValue(id) != null;
                    } catch (RegistryException e) {
                        return false;
                    }
                })
                .label(TextObject.translation("bubbleblaster.screen.options.graphical.gameHud"))
                .bounds(this.middleX + 1, this.middleY + 101, 300, 48)
                .entryWidth(200).build());
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
