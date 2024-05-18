package dev.ultreon.bubbles.render.gui.screen.options;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.BubbleBlasterConfig;
import dev.ultreon.bubbles.common.DifficultyEffectType;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.bubbles.render.gui.widget.Button;
import dev.ultreon.bubbles.render.gui.widget.CycleButton;
import dev.ultreon.bubbles.render.gui.widget.DecimalNumberInput;
import dev.ultreon.bubbles.render.gui.widget.NumberSlider;
import dev.ultreon.bubbles.text.Translations;
import dev.ultreon.libs.text.v1.TextObject;

import static dev.ultreon.bubbles.BubbleBlasterConfig.*;

public class GameplayOptionsScreen extends Screen {
    private NumberSlider timedModeLimit;
    private CycleButton<DifficultyEffectType> difficultyEffectType;
    private DecimalNumberInput baseBubbleSpeed;
    private NumberSlider boostDuration;
    private NumberSlider shootCooldown;
    private NumberSlider boostCooldown;
    private NumberSlider maxBubbles;
    private NumberSlider levelThreshold;
    private DecimalNumberInput scoreReductionIndirect;
    private DecimalNumberInput scoreReductionDirect;
    private Button cancelButton;
    private Button saveButton;

    public GameplayOptionsScreen(Screen back) {
        super(back);
    }

    public void save() {
        BASE_BUBBLE_SPEED.set(this.baseBubbleSpeed.getValue());
        TIME_LIMIT.set(this.timedModeLimit.getValue());
        DIFFICULTY_EFFECT_TYPE.set(this.difficultyEffectType.getValue());
        BOOST_DURATION.set(this.boostDuration.getValue());
        SHOOT_COOLDOWN.set(this.shootCooldown.getValue());
        BOOST_COOLDOWN.set(this.boostCooldown.getValue());
        MAX_BUBBLES.set(this.maxBubbles.getValue());
        LEVEL_THRESHOLD.set(this.levelThreshold.getValue());
        BUBBLE_SCORE_REDUCTION.set(this.scoreReductionDirect.getValue());
        BUBBLE_SCORE_REDUCTION_SELF.set(this.scoreReductionIndirect.getValue());
        BubbleBlasterConfig.save();
        this.back();
    }

    @Override
    public void init() {
        var entryWidth = 100;
        this.timedModeLimit = this.add(NumberSlider.builder()
                .value(TIME_LIMIT)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.timedMode.timeLimit"))
                .entryWidth(entryWidth).bounds(this.middleX - 301, this.middleY - 99, 300, 48).build());
        this.difficultyEffectType = this.add(CycleButton.builder(DifficultyEffectType.class)
                .text(TextObject.translation("bubbleblaster.screen.options.gameplay.difficultyEffect"))
                .bounds(this.middleX + 1, this.middleY - 99, 300, 48).build());
        this.baseBubbleSpeed = this.add(DecimalNumberInput.builder()
                .value(BASE_BUBBLE_SPEED)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.baseBubbleSpeed"))
                .bounds(this.middleX - 301, this.middleY - 49, 300, 48)
                .entryWidth(entryWidth).build());
        this.boostDuration = this.add(NumberSlider.builder()
                .value(BOOST_DURATION)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.boostDuration"))
                .entryWidth(entryWidth).bounds(this.middleX + 1, this.middleY - 49, 300, 48).build());
        this.shootCooldown = this.add(NumberSlider.builder()
                .value(SHOOT_COOLDOWN)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.shootCooldown"))
                .bounds(this.middleX - 301, this.middleY + 1, 300, 48)
                .entryWidth(entryWidth).build());
        this.boostCooldown = this.add(NumberSlider.builder()
                .value(BOOST_COOLDOWN)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.boostCooldown"))
                .bounds(this.middleX + 1, this.middleY + 1, 300, 48)
                .entryWidth(entryWidth).build());
        this.scoreReductionIndirect = this.add(DecimalNumberInput.builder()
                .value(BUBBLE_SCORE_REDUCTION)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.scoreReductionIndirect"))
                .bounds(this.middleX - 301, this.middleY + 51, 300, 48)
                .entryWidth(entryWidth).build());
        this.scoreReductionDirect = this.add(DecimalNumberInput.builder()
                .value(BUBBLE_SCORE_REDUCTION_SELF)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.scoreReductionDirect"))
                .bounds(this.middleX + 1, this.middleY + 51, 300, 48)
                .entryWidth(entryWidth).build());
        this.maxBubbles = this.add(NumberSlider.builder()
                .value(MAX_BUBBLES)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.maxBubbles"))
                .bounds(this.middleX - 301, this.middleY + 101, 300, 48)
                .entryWidth(entryWidth).build());
        this.levelThreshold = this.add(NumberSlider.builder()
                .value(LEVEL_THRESHOLD)
                .label(TextObject.translation("bubbleblaster.screen.options.gameplay.levelThreshold"))
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
