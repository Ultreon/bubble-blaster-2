package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.options.OptionsScreen;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.text.Translations;
import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.bubbles.world.WorldRenderer;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.text.v1.TextObject;
import com.ultreon.libs.translations.v1.Language;

import java.util.ArrayList;

public class PauseScreen extends Screen {
    private static final Color DETAIL_NAME_COLOR = Color.WHITE.withAlpha(0x80);
    private static final Color DETAIL_VALUE_COLOR = Color.WHITE.withAlpha(0x60);
    private Button forfeitButton;
    private Button optionsButton;
    private Button prevButton;
    private Button nextButton;

    private final TextObject id = TextObject.translation("bubbleblaster.screen.pause.id");
    private final TextObject hardness = TextObject.translation("bubbleblaster.screen.pause.hardness");
    private final TextObject radius = TextObject.translation("bubbleblaster.screen.pause.radius");
    private final TextObject speed = TextObject.translation("bubbleblaster.screen.pause.speed");
    private final TextObject curChance = TextObject.translation("bubbleblaster.screen.pause.chance");
    private final TextObject curPriority = TextObject.translation("bubbleblaster.screen.pause.priority");
    private final TextObject scoreMod = TextObject.translation("bubbleblaster.screen.pause.score");
    private final TextObject attackMod = TextObject.translation("bubbleblaster.screen.pause.damage");
    private final TextObject defenseMod = TextObject.translation("bubbleblaster.screen.pause.defense");
    private final TextObject canSpawn = TextObject.translation("bubbleblaster.screen.pause.canSpawn");
    private final TextObject description = TextObject.translation("bubbleblaster.screen.pause.description");
    private final TextObject boolTrue = TextObject.translation("bubbleblaster.misc.true");
    private final TextObject boolFalse = TextObject.translation("bubbleblaster.misc.false");

    private final int registeredBubbles;
    private static int helpIndex = 0;
    private BubbleType bubble;


    static {
        GameEvents.LANGUAGE_CHANGED.listen((from, to) -> {
            if (BubbleBlaster.getInstance().getCurrentScreen() instanceof PauseScreen) {
                var pauseScreen = (PauseScreen) BubbleBlaster.getInstance().getCurrentScreen();
                pauseScreen.changeLanguage();
            }
        });
    }

    private final TextObject title;

    public PauseScreen() {
        super();

        this.title = TextObject.translation("bubbleblaster.screen.pause.text");

        this.registeredBubbles = Registries.BUBBLES.values().size();
    }

    private void showOptions() {
        this.game.showScreen(new OptionsScreen());
    }

    private void previousPage() {
        helpIndex = Mth.clamp(helpIndex - 1, 0, this.registeredBubbles - 1);
        this.tickPage();
    }

    private void nextPage() {
        helpIndex = Mth.clamp(helpIndex + 1, 0, this.registeredBubbles - 1);
        this.tickPage();
    }

    private void tickPage() {
        this.bubble = new ArrayList<>(Registries.BUBBLES.values()).get(helpIndex);

        if (helpIndex >= this.registeredBubbles - 1) {
            this.nextButton.enabled = false;
            this.nextButton.visible = false;
        } else {
            this.nextButton.enabled = true;
            this.nextButton.visible = true;
        }

        if (helpIndex <= 0) {
            this.prevButton.enabled = false;
            this.prevButton.visible = false;
        } else {
            this.prevButton.enabled = true;
            this.prevButton.visible = true;
        }
    }

    private void changeLanguage() {

    }

    @Override
    public void init() {
        this.clearWidgets();

        this.forfeitButton = this.add(Button.builder().bounds(this.middleX - 175, 200, 350, 48).text(TextObject.translation("bubbleblaster.screen.pause.forfeit")).command(this.game::saveAndQuit).build());
        this.optionsButton = this.add(Button.builder().bounds(this.middleX - 175, 250, 350, 48).text(TextObject.translation("bubbleblaster.screen.title.options")).command(this::showOptions).build());
        this.prevButton = this.add(Button.builder().bounds(this.middleX - 480, 250, 96, 48).text(Translations.PREV).command(this::previousPage).build());
        this.nextButton = this.add(Button.builder().bounds(this.middleX + 480 - 95, 250, 96, 48).text(Translations.NEXT).command(this::nextPage).build());

        this.tickPage();
    }

    @Override
    public boolean close(Screen to) {
        return super.close(to);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        var loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame == null) {
            return;
        }

        // Render background
        this.renderBackground(renderer);

        // Pause text
        renderer.drawTextCenter(Fonts.DONGLE_75.get(), this.title, this.width / 2f, 120f, Color.argb(0x80ffffff));

        // Render widgets.
        this.prevButton.visible = PauseScreen.helpIndex > 0;
        this.nextButton.visible = PauseScreen.helpIndex < this.registeredBubbles - 1;

        this.renderChildren(renderer, mouseX, mouseY, deltaTime);

        // Border
        renderer.box(this.middleX - 480, 300, 960, 300, Color.WHITE.withAlpha(0x80));

        // Bubble name.
        renderer.drawTextLeft(Fonts.SANS_BOLD_32.get(), this.bubble.getTranslation(), this.middleX - 470, 322, DETAIL_NAME_COLOR);

        // Bubble icon.
        WorldRenderer.drawBubble(renderer, this.middleX - 409, 411, 122, 0, this.bubble);

        //********************//
        //     Info Names     //
        //********************//

        // Left data.
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.id, this.middleX - 326 + 10, 362, DETAIL_NAME_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.hardness, this.middleX - 326 + 10, 382, DETAIL_NAME_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.radius, this.middleX - 326 + 10, 402, DETAIL_NAME_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.speed, this.middleX - 326 + 10, 422, DETAIL_NAME_COLOR);

        // Right data.
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.curChance, this.middleX + 72 + 10, 322, DETAIL_NAME_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.curPriority, this.middleX + 72 + 10, 342, DETAIL_NAME_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.scoreMod, this.middleX + 72 + 10, 362, DETAIL_NAME_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.attackMod, this.middleX + 72 + 10, 382, DETAIL_NAME_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.defenseMod, this.middleX + 72 + 10, 402, DETAIL_NAME_COLOR);
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.canSpawn, this.middleX + 72 + 10, 422, DETAIL_NAME_COLOR);

        // Description
        renderer.drawTextLeft(Fonts.SANS_BOLD_16.get(), this.description, this.middleX - 470, 502, DETAIL_NAME_COLOR);

        //****************//
        //     Values     //
        //****************//

        // Left data.
        float leftX = this.middleX - 326 + 200;
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), this.bubble.getId().toString(), leftX, 362, DETAIL_VALUE_COLOR);
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), this.bubble.getHardness().getTranslation(), leftX, 382, DETAIL_VALUE_COLOR);
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), this.bubble.getRadius().getTranslation(), leftX, 402, DETAIL_VALUE_COLOR);
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), this.bubble.getSpeed().getTranslation(), leftX, 422, DETAIL_VALUE_COLOR);

        // Right data
        float rightX = this.middleX + 72 + 200;
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), MathHelper.toReadableString((double) 100 * BubbleSystem.getPercentageChance(this.bubble), 5) + "%", rightX, 322, DETAIL_VALUE_COLOR);
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), MathHelper.compress(BubbleSystem.getPriority(this.bubble)) + " / " +  MathHelper.compress(BubbleSystem.getTotalPriority()), rightX, 342, DETAIL_VALUE_COLOR);
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), this.bubble.getScore().getTranslation(), rightX, 362, DETAIL_VALUE_COLOR);
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), this.bubble.getAttack().getTranslation(), rightX, 382, DETAIL_VALUE_COLOR);
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), this.bubble.getDefense().getTranslation(), rightX, 402, DETAIL_VALUE_COLOR);
        renderer.drawTextLeft(Fonts.SANS_ITALIC_16.get(), this.bubble.canSpawn(loadedGame.getWorld()) ? this.boolTrue : this.boolFalse, rightX, 422, DETAIL_NAME_COLOR);

        // Description
        renderer.drawWrappedText(Fonts.SANS_ITALIC_16.get(), Language.translate(this.bubble.getDescriptionTranslationPath()).replaceAll("\\\\n", "\n"), this.middleX - 470, 512, 940, DETAIL_VALUE_COLOR);
    }
}
