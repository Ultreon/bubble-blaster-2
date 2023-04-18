package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.common.text.TranslationText;
import com.ultreon.libs.translations.v0.Language;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.environment.EnvironmentRenderer;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.font.FontStyle;
import com.ultreon.bubbles.render.gui.widget.IngameButton;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.util.helpers.Mth;

import java.util.ArrayList;

public class PauseScreen extends Screen {
    private IngameButton exitButton;
    private IngameButton forfeitButton;
    private IngameButton prevButton;
    private IngameButton nextButton;

    private final TextObject minRadius = new TranslationText("bubbles/screen/pause/min_radius");
    private final TextObject maxRadius = new TranslationText("bubbles/screen/pause/max_radius");
    private final TextObject minSpeed = new TranslationText("bubbles/screen/pause/min_speed");
    private final TextObject maxSpeed = new TranslationText("bubbles/screen/pause/max_speed");
    private final TextObject defChance = new TranslationText("bubbles/screen/pause/default_chance");
    private final TextObject curChance = new TranslationText("bubbles/screen/pause/current_chance");
    private final TextObject defPriority = new TranslationText("bubbles/screen/pause/default_priority");
    private final TextObject curPriority = new TranslationText("bubbles/screen/pause/current_priority");
    private final TextObject defTotPriority = new TranslationText("bubbles/screen/pause/default_total_priority");
    private final TextObject curTotPriority = new TranslationText("bubbles/screen/pause/current_total_priority");
    private final TextObject scoreMod = new TranslationText("bubbles/screen/pause/score_modifier");
    private final TextObject attackMod = new TranslationText("bubbles/screen/pause/attack_modifier");
    private final TextObject defenseMod = new TranslationText("bubbles/screen/pause/defense_modifier");
    private final TextObject canSpawn = new TranslationText("bubbles/screen/pause/can_spawn");
    private final TextObject description = new TranslationText("bubbles/screen/pause/description");
    private final TextObject random = new TranslationText("bubbles/other/random");
    private final TextObject boolTrue = new TranslationText("bubbles/other/true");
    private final TextObject boolFalse = new TranslationText("bubbles/other/False");

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

        Util.setCursor(this.game.getDefaultCursor());
    }

    @Override
    public boolean onClose(Screen to) {
        clearWidgets();

        Util.setCursor(BubbleBlaster.getInstance().getBlankCursor());
        return super.onClose(to);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame == null) {
            return;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Darkened background     //
        /////////////////////////////////
        renderer.color(Color.argb(0xc0000000));
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Pause text     //
        ////////////////////////
        renderer.color(Color.argb(0x80ffffff));
        game.getLogoFont().draw(renderer, Language.translate("bubbles/screen/pause/text"), 75, (float)width / 2, 90 + 75f / 2, Anchor.CENTER);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Exit button     //
        /////////////////////////
        exitButton.setText(Language.translate("bubbles/screen/pause/exit"));
        exitButton.render(renderer.subInstance(exitButton.getBounds()));

        forfeitButton.setText(Language.translate("bubbles/screen/pause/forfeit"));
        forfeitButton.render(renderer.subInstance(forfeitButton.getBounds()));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Navigation Buttons & border     //
        /////////////////////////////////////////

        // Navigation buttons.
        nextButton.setText(Language.translate("bubbles/other/next"));
        prevButton.setText(Language.translate("bubbles/other/prev"));

        if (helpIndex > 0) prevButton.render(renderer.subInstance(prevButton.getBounds()));
        if (helpIndex < differentBubbles - 1) nextButton.render(renderer.subInstance(nextButton.getBounds()));

        // Border
        renderer.color(Color.argb(0x80ffffff));
        renderer.rectLine((int) (BubbleBlaster.getMiddleX() - 480), 300, 960, 300);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Bubble     //
        ////////////////////

        // Bubble name.
        renderer.color(Color.argb(0xc0ffffff));
        font.draw(renderer, Language.translate(bubble.getTranslationPath()), 32, (int) BubbleBlaster.getMiddleX() - 470, 332, Anchor.W);

        // Bubble icon.
        EnvironmentRenderer.drawBubble(renderer, (int) (BubbleBlaster.getMiddleX() - 470), 350, 122, bubble.getColors());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Info Names     //
        ////////////////////////

        // Set color & font.
        renderer.color(Color.argb(0xc0ffffff));

        // Left data.
        font.draw(renderer, minRadius, 16, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 362, Anchor.W);
        font.draw(renderer, maxRadius, 16, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 382, Anchor.W);
        font.draw(renderer, minSpeed, 16, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 402, Anchor.W);
        font.draw(renderer, maxSpeed, 16, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 422, Anchor.W);
        font.draw(renderer, defChance, 16, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 442, Anchor.W);
        font.draw(renderer, curChance, 16, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 462, Anchor.W);

        // Right data.
        font.draw(renderer, defTotPriority, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 322, Anchor.W);
        font.draw(renderer, curTotPriority, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 342, Anchor.W);
        font.draw(renderer, defPriority, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 362, Anchor.W);
        font.draw(renderer, curPriority, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 382, Anchor.W);
        font.draw(renderer, scoreMod, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 402, Anchor.W);
        font.draw(renderer, attackMod, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 422, Anchor.W);
        font.draw(renderer, defenseMod, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 442, Anchor.W);
        font.draw(renderer, canSpawn, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 462, Anchor.W);

        // Description
        renderer.text(description, (int) BubbleBlaster.getMiddleX() - 470, 502);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Values     //
        ////////////////////

        // Set color & font.
        renderer.color(Color.argb(0x80ffffff));

        // Left data.
        font.draw(renderer, Integer.toString(bubble.getMinRadius()), 16, (int) (BubbleBlaster.getMiddleX() - 326) + 200, 362, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, Integer.toString(bubble.getMaxRadius()), 16, (int) (BubbleBlaster.getMiddleX() - 326) + 200, 382, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, Double.toString(Mth.round(bubble.getMinSpeed(), 5)), 16, (int) (BubbleBlaster.getMiddleX() - 326) + 200, 402, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, Double.toString(Mth.round(bubble.getMaxSpeed(), 5)), 16, (int) (BubbleBlaster.getMiddleX() - 326) + 200, 422, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, Mth.round((double) 100 * BubbleSystem.getDefaultPercentageChance(bubble), 5) + "%", 16, (int) (BubbleBlaster.getMiddleX() - 326) + 200, 442, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, Mth.round((double) 100 * BubbleSystem.getPercentageChance(bubble), 5) + "%", 16, (int) (BubbleBlaster.getMiddleX() - 326) + 200, 462, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, compress(BubbleSystem.getDefaultTotalPriority()), 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 322, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, compress(BubbleSystem.getTotalPriority()), 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 342, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, compress(BubbleSystem.getDefaultPriority(bubble)), 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 362, FontStyle.ITALIC, Anchor.W);
        font.draw(renderer, compress(BubbleSystem.getPriority(bubble)), 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 382, FontStyle.ITALIC, Anchor.W);

        // Right data
        if (bubble.isScoreRandom()) {
            font.draw(renderer, random, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 402, FontStyle.ITALIC);
        } else {
            font.draw(renderer, Double.toString(Mth.round(bubble.getScore(), 5)), 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 402, FontStyle.ITALIC, Anchor.W);
        }
        if (bubble.isAttackRandom()) {
            font.draw(renderer, random, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 422, FontStyle.ITALIC);
        } else {
            font.draw(renderer, Double.toString(Mth.round(bubble.getAttack(), 5)), 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 422, FontStyle.ITALIC, Anchor.W);
        }
        if (bubble.isDefenseRandom()) {
            font.draw(renderer, random, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 442, FontStyle.ITALIC);
        } else {
            font.draw(renderer, Double.toString(Mth.round(bubble.getDefense(), 5)), 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 442, FontStyle.ITALIC, Anchor.W);
        }
        font.draw(renderer, bubble.canSpawn(loadedGame.getEnvironment()) ? boolTrue : boolFalse, 16, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 462, FontStyle.ITALIC, Anchor.W);

        // Description
        font.drawMultiline(renderer, font.wrap(16, Language.translate(bubble.getDescriptionTranslationPath()).replaceAll("\\\\n", "\n"), 940), 16, (int) BubbleBlaster.getMiddleX() - 470, 522, FontStyle.ITALIC, Anchor.NW);
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
