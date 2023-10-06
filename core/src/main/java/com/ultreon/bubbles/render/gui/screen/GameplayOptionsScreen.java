package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.common.DifficultyEffectType;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.render.gui.widget.CycleButton;
import com.ultreon.bubbles.render.gui.widget.DecimalNumberInput;
import com.ultreon.bubbles.render.gui.widget.NumberSlider;
import com.ultreon.bubbles.text.Translations;
import com.ultreon.libs.text.v1.TextObject;

import static com.ultreon.bubbles.BubbleBlasterConfig.*;

public class GameplayOptionsScreen extends Screen {
    private DecimalNumberInput baseBubbleSpeed;
    private CycleButton<DifficultyEffectType> difficultyEffectType;
    private NumberSlider shootCooldown;
    private NumberSlider boostCooldown;
    private NumberSlider maxBubbles;
    private NumberSlider levelThredhold;
    private DecimalNumberInput scoreReductionIndirect;
    private DecimalNumberInput scoreReductionDirect;
    private Button languageButton;
    private Button cancelButton;
    private Button saveButton;
    private final Screen back;

    public GameplayOptionsScreen(Screen back) {
        super();

        this.back = back;
    }

    public void save() {
        BASE_BUBBLE_SPEED.set(this.baseBubbleSpeed.getValue());
        DIFFICULTY_EFFECT_TYPE.set(this.difficultyEffectType.getValue());
        SHOOT_COOLDOWN.set(this.shootCooldown.getValue());
        BOOST_COOLDOWN.set(this.boostCooldown.getValue());
        MAX_BUBBLES.set(this.maxBubbles.getValue());
        LEVEL_THRESHOLD.set(this.levelThredhold.getValue());
        BUBBLE_SCORE_REDUCTION.set(this.scoreReductionIndirect.getValue());
        BUBBLE_SCORE_REDUCTION_SELF.set(this.scoreReductionIndirect.getValue());
        BubbleBlasterConfig.save();
        this.back();
    }

    public void back() {
        this.game.showScreen(this.back);
    }

    @Override
    public void init() {
        this.baseBubbleSpeed = this.add(DecimalNumberInput.builder()
                .value(BASE_BUBBLE_SPEED)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.baseBubbleSpeed"))
                .bounds(this.middleX - 301, this.middleY - 49, 300, 48)
                .entryWidth(80).build());
        this.difficultyEffectType = this.add(CycleButton.builder(DifficultyEffectType.class)
                .text(TextObject.translation("bubbleblaster.screen.options.gameplay.difficultyEffect"))
                .bounds(this.middleX + 1, this.middleY - 49, 300, 48).build());
        this.shootCooldown = this.add(NumberSlider.builder()
                .value(SHOOT_COOLDOWN)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.shootCooldown"))
                .bounds(this.middleX - 301, this.middleY + 1, 300, 48)
                .entryWidth(80).build());
        this.boostCooldown = this.add(NumberSlider.builder()
                .value(BOOST_COOLDOWN)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.boostCooldown"))
                .bounds(this.middleX + 1, this.middleY + 1, 300, 48)
                .entryWidth(80).build());
        this.scoreReductionIndirect = this.add(DecimalNumberInput.builder()
                .value(BUBBLE_SCORE_REDUCTION)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.scoreReductionIndirect"))
                .bounds(this.middleX - 301, this.middleY + 51, 300, 48)
                .entryWidth(80).build());
        this.scoreReductionDirect = this.add(DecimalNumberInput.builder()
                .value(BUBBLE_SCORE_REDUCTION_SELF)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.scoreReductionIndirect"))
                .bounds(this.middleX + 1, this.middleY + 51, 300, 48)
                .entryWidth(80).build());
        this.maxBubbles = this.add(NumberSlider.builder()
                .value(MAX_BUBBLES)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.maxBubbles"))
                .bounds(this.middleX - 301, this.middleY + 101, 300, 48)
                .entryWidth(80).build());
        this.levelThredhold = this.add(NumberSlider.builder()
                .value(LEVEL_THRESHOLD)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.levelThreshold"))
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
