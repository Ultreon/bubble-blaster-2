package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.environment.EnvironmentRenderer;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.IngameButton;
import com.ultreon.bubbles.util.Utils;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.libs.text.v0.TextObject;
import com.ultreon.libs.translations.v0.Language;

import java.util.ArrayList;

public class PauseScreen extends Screen {
    private IngameButton exitButton;
    private IngameButton forfeitButton;
    private IngameButton prevButton;
    private IngameButton nextButton;

    private final TextObject minRadius = TextObject.translation("bubbles/screen/pause/min_radius");
    private final TextObject maxRadius = TextObject.translation("bubbles/screen/pause/max_radius");
    private final TextObject minSpeed = TextObject.translation("bubbles/screen/pause/min_speed");
    private final TextObject maxSpeed = TextObject.translation("bubbles/screen/pause/max_speed");
    private final TextObject defChance = TextObject.translation("bubbles/screen/pause/default_chance");
    private final TextObject curChance = TextObject.translation("bubbles/screen/pause/current_chance");
    private final TextObject defPriority = TextObject.translation("bubbles/screen/pause/default_priority");
    private final TextObject curPriority = TextObject.translation("bubbles/screen/pause/current_priority");
    private final TextObject defTotPriority = TextObject.translation("bubbles/screen/pause/default_total_priority");
    private final TextObject curTotPriority = TextObject.translation("bubbles/screen/pause/current_total_priority");
    private final TextObject scoreMod = TextObject.translation("bubbles/screen/pause/score_modifier");
    private final TextObject attackMod = TextObject.translation("bubbles/screen/pause/attack_modifier");
    private final TextObject defenseMod = TextObject.translation("bubbles/screen/pause/defense_modifier");
    private final TextObject canSpawn = TextObject.translation("bubbles/screen/pause/can_spawn");
    private final TextObject description = TextObject.translation("bubbles/screen/pause/description");
    private final TextObject random = TextObject.translation("bubbles/other/random");
    private final TextObject boolTrue = TextObject.translation("bubbles/other/true");
    private final TextObject boolFalse = TextObject.translation("bubbles/other/False");

    private final int differentBubbles;
    private static int helpIndex = 0;
    private BubbleType bubble;


    static {
        GameEvents.LANGUAGE_CHANGED.listen((from, to) -> {
            if (BubbleBlaster.getInstance().getCurrentScreen() instanceof PauseScreen pauseScreen) {
                pauseScreen.changeLanguage();
            }
        });
    }

    public PauseScreen() {
        super();

        exitButton = new IngameButton.Builder().bounds((int) (BubbleBlaster.getMiddleX() - 128), 200, 256, 48).text("Exit and Quit Game").command(this.game::shutdown).build();
        forfeitButton = new IngameButton.Builder().bounds((int) (BubbleBlaster.getMiddleX() - 128), 250, 256, 48).text("Save and Go To Title").command(this.game::saveAndQuit).build();
        prevButton = new IngameButton.Builder().bounds((int) (BubbleBlaster.getMiddleX() - 480), 250, 96, 48).text("Prev").command(this::previousPage).build();
        nextButton = new IngameButton.Builder().bounds((int) (BubbleBlaster.getMiddleX() + 480 - 95), 250, 96, 48).text("Next").command(this::nextPage).build();

        differentBubbles = Registries.BUBBLES.values().size();
        tickPage();
    }

    private void previousPage() {
        helpIndex = Mth.clamp(helpIndex - 1, 0, differentBubbles - 1);
        tickPage();
    }

    private void nextPage() {
        helpIndex = Mth.clamp(helpIndex + 1, 0, differentBubbles - 1);
        tickPage();
    }

    private void tickPage() {
        bubble = new ArrayList<>(Registries.BUBBLES.values()).get(helpIndex);

        if (helpIndex >= differentBubbles - 1 && nextButton.isValid()) {
            nextButton.enabled = false;
            nextButton.visible = false;
        } else if (!nextButton.isValid()) {
            nextButton.enabled = true;
            nextButton.visible = true;
        }

        if (helpIndex <= 0 && prevButton.isValid()) {
            prevButton.enabled = false;
            prevButton.visible = false;
        } else if (!prevButton.isValid()) {
            prevButton.enabled = true;
            prevButton.visible = true;
        }
    }

    private void changeLanguage() {

    }

    @Override
    public void init() {
        this.clearWidgets();

        this.exitButton = add(this.exitButton);
        this.forfeitButton = add(this.forfeitButton);
        this.prevButton = add(this.prevButton);
        this.nextButton = add(this.nextButton);

        if (!this.game.isInGame()) {
            return;
        }

        Utils.showCursor();
    }

    @Override
    public boolean onClose(Screen to) {
        clearWidgets();

        if (to == null) Utils.hideCursor();
        return super.onClose(to);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame == null) {
            return;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Darkened background     //
        /////////////////////////////////
        renderer.setColor(Color.argb(0xc0000000));
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Pause text     //
        ////////////////////////
        renderer.setColor(Color.argb(0x80ffffff));
        renderer.setFont(Fonts.DONGLE_75.get());
        renderer.drawCenteredText(Language.translate("bubbles/screen/pause/text"), 75, (float)width / 2);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Exit button     //
        /////////////////////////
        exitButton.setText(Language.translate("bubbles/screen/pause/exit"));
        renderer.subInstance(exitButton.getBounds(), subRender -> exitButton.render(subRender, mouseX, mouseY, deltaTime));

        forfeitButton.setText(Language.translate("bubbles/screen/pause/forfeit"));
        renderer.subInstance(forfeitButton.getBounds(), subRender -> forfeitButton.render(subRender, mouseX, mouseY, deltaTime));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Navigation Buttons & border     //
        /////////////////////////////////////////

        // Navigation buttons.
        nextButton.setText(Language.translate("bubbles/other/next"));
        prevButton.setText(Language.translate("bubbles/other/prev"));

        if (helpIndex > 0) renderer.subInstance(prevButton.getBounds(), subRender -> prevButton.render(subRender, mouseX, mouseY, deltaTime));
        if (helpIndex < differentBubbles - 1) renderer.subInstance(nextButton.getBounds(), subRender -> nextButton.render(subRender, mouseX, mouseY, deltaTime));

        // Border
        renderer.setColor(Color.argb(0x80ffffff));
        renderer.rectLine((int) (BubbleBlaster.getMiddleX() - 480), 300, 960, 300);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Bubble     //
        ////////////////////

        // Bubble name.
        renderer.setColor(Color.argb(0xc0ffffff));
        renderer.drawText(Fonts.SANS_REGULAR_32.get(), Language.translate(bubble.getTranslationPath()), BubbleBlaster.getMiddleX() - 470, 332, Anchor.W);

        // Bubble icon.
        EnvironmentRenderer.drawBubble(renderer, (int) (BubbleBlaster.getMiddleX() - 470), 350, 122, bubble.getColors());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Info Names     //
        ////////////////////////

        // Set color & font.
        renderer.setColor(Color.argb(0xc0ffffff));

        // Left data.
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), minRadius, (BubbleBlaster.getMiddleX() - 326) + 10, 362, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), maxRadius, (BubbleBlaster.getMiddleX() - 326) + 10, 382, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), minSpeed, (BubbleBlaster.getMiddleX() - 326) + 10, 402, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), maxSpeed, (BubbleBlaster.getMiddleX() - 326) + 10, 422, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), defChance, (BubbleBlaster.getMiddleX() - 326) + 10, 442, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), curChance, (BubbleBlaster.getMiddleX() - 326) + 10, 462, Anchor.W);

        // Right data.
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), defTotPriority, BubbleBlaster.getMiddleX() + 72 + 10, 322, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), curTotPriority, BubbleBlaster.getMiddleX() + 72 + 10, 342, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), defPriority, BubbleBlaster.getMiddleX() + 72 + 10, 362, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), curPriority, BubbleBlaster.getMiddleX() + 72 + 10, 382, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), scoreMod, BubbleBlaster.getMiddleX() + 72 + 10, 402, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), attackMod, BubbleBlaster.getMiddleX() + 72 + 10, 422, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), defenseMod, BubbleBlaster.getMiddleX() + 72 + 10, 442, Anchor.W);
        renderer.drawText(Fonts.SANS_REGULAR_16.get(), canSpawn, BubbleBlaster.getMiddleX() + 72 + 10, 462, Anchor.W);

        // Description
        renderer.drawText(description, (int) BubbleBlaster.getMiddleX() - 470, 502);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Values     //
        ////////////////////

        // Set color & font.
        renderer.setColor(Color.argb(0x80ffffff));

        // Left data.
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), Integer.toString(bubble.getMinRadius()), (BubbleBlaster.getMiddleX() - 326) + 200, 362, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), Integer.toString(bubble.getMaxRadius()), (BubbleBlaster.getMiddleX() - 326) + 200, 382, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), Double.toString(Mth.round(bubble.getMinSpeed(), 5)), (BubbleBlaster.getMiddleX() - 326) + 200, 402, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), Double.toString(Mth.round(bubble.getMaxSpeed(), 5)), (BubbleBlaster.getMiddleX() - 326) + 200, 422, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), Mth.round((double) 100 * BubbleSystem.getDefaultPercentageChance(bubble), 5) + "%", (BubbleBlaster.getMiddleX() - 326) + 200, 442, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), Mth.round((double) 100 * BubbleSystem.getPercentageChance(bubble), 5) + "%", (BubbleBlaster.getMiddleX() - 326) + 200, 462, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), compress(BubbleSystem.getDefaultTotalPriority()), BubbleBlaster.getMiddleX() + 72 + 200, 322, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), compress(BubbleSystem.getTotalPriority()), BubbleBlaster.getMiddleX() + 72 + 200, 342, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), compress(BubbleSystem.getDefaultPriority(bubble)), BubbleBlaster.getMiddleX() + 72 + 200, 362, Anchor.W);
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), compress(BubbleSystem.getPriority(bubble)), BubbleBlaster.getMiddleX() + 72 + 200, 382, Anchor.W);

        // Right data
        if (bubble.isScoreRandom()) {
            renderer.drawText(Fonts.SANS_ITALIC_16.get(), random, BubbleBlaster.getMiddleX() + 72 + 200, 402);
        } else {
            renderer.drawText(Fonts.SANS_ITALIC_16.get(), Double.toString(Mth.round(bubble.getScore(), 5)), BubbleBlaster.getMiddleX() + 72 + 200, 402, Anchor.W);
        }
        if (bubble.isAttackRandom()) {
            renderer.drawText(Fonts.SANS_ITALIC_16.get(), random, BubbleBlaster.getMiddleX() + 72 + 200, 422);
        } else {
            renderer.drawText(Fonts.SANS_ITALIC_16.get(), Double.toString(Mth.round(bubble.getAttack(), 5)), BubbleBlaster.getMiddleX() + 72 + 200, 422, Anchor.W);
        }
        if (bubble.isDefenseRandom()) {
            renderer.drawText(Fonts.SANS_ITALIC_16.get(), random, BubbleBlaster.getMiddleX() + 72 + 200, 442);
        } else {
            renderer.drawText(Fonts.SANS_ITALIC_16.get(), Double.toString(Mth.round(bubble.getDefense(), 5)), BubbleBlaster.getMiddleX() + 72 + 200, 442, Anchor.W);
        }
        renderer.drawText(Fonts.SANS_ITALIC_16.get(), bubble.canSpawn(loadedGame.getEnvironment()) ? boolTrue : boolFalse, BubbleBlaster.getMiddleX() + 72 + 200, 462, Anchor.W);

        // Description
        renderer.drawWrappedText(Fonts.SANS_ITALIC_16.get(), Language.translate(bubble.getDescriptionTranslationPath()).replaceAll("\\\\n", "\n"), (int) BubbleBlaster.getMiddleX() - 470, 522, 940);
    }

    private String compress(double totalPriority) {
        if (totalPriority >= 0d && totalPriority < 1_000d) {
            return Double.toString(totalPriority);
        }
        if (totalPriority >= 1_000d && totalPriority < 1_000_000d) {
            return Mth.round(totalPriority / 1_000d, 1) + "K";
        }
        if (totalPriority >= 1_000_000d && totalPriority < 1_000_000_000d) {
            return Mth.round(totalPriority / 1_000_000d, 1) + "M";
        }
        if (totalPriority >= 1_000_000_000d && totalPriority < 1_000_000_000_000d) {
            return Mth.round(totalPriority / 1_000_000_000d, 1) + "B";
        }
        if (totalPriority >= 1_000_000_000_000d && totalPriority < 1_000_000_000_000_000d) {
            return Mth.round(totalPriority / 1_000_000_000_000d, 1) + "T";
        }
        if (totalPriority >= 1_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000d) {
            return Mth.round(totalPriority / 1_000_000_000_000_000d, 1) + "QD";
        }
        if (totalPriority >= 1_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000d) {
            return Mth.round(totalPriority / 1_000_000_000_000_000_000d, 1) + "QT";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000d) {
            return Mth.round(totalPriority / 1_000_000_000_000_000_000_000d, 1) + "S";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000_000d) {
            return Mth.round(totalPriority / 1_000_000_000_000_000_000_000_000d, 1) + "SX";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000_000_000d) {
            return Mth.round(totalPriority / 1_000_000_000_000_000_000_000_000_000d, 1) + "C";
        }
        return Double.toString(totalPriority);
    }

    @Override
    public boolean doesPauseGame() {
        return true;
    }
}
