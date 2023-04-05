package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.common.text.TranslationText;
import com.ultreon.bubbles.common.text.translation.Language;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.environment.EnvironmentRenderer;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.media.SoundInstance;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.IngameButton;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.util.helpers.Mth;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Objects;

public class PauseScreen extends Screen {
    private final IngameButton exitButton;
    private final IngameButton prevButton;
    private final IngameButton nextButton;

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

    private final Font bubbleTitleFont = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD, 32);
    private final Font bubbleValueFont = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD + Font.ITALIC, 16);
    private final Font bubbleInfoFont = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD, 16);
    private final Font fallbackTitleFont = new Font(BubbleBlaster.getInstance().getFont().getFontName(), Font.BOLD, 32);
    private final Font fallbackValueFont = new Font(BubbleBlaster.getInstance().getFont().getFontName(), Font.BOLD + Font.ITALIC, 16);
    private final Font fallbackInfoFont = new Font(BubbleBlaster.getInstance().getFont().getFontName(), Font.BOLD, 16);

    private final Font pauseFont = new Font(BubbleBlaster.getInstance().getGameFontName(), Font.PLAIN, 75);
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
        exitButton = new IngameButton.Builder().bounds((int) (BubbleBlaster.getMiddleX() - 128), 200, 256, 48).text("Exit and Quit Game").command(BubbleBlaster.getInstance()::shutdown).build();
        prevButton = new IngameButton.Builder().bounds((int) (BubbleBlaster.getMiddleX() - 480), 250, 96, 48).text("Prev").command(this::previousPage).build();
        nextButton = new IngameButton.Builder().bounds((int) (BubbleBlaster.getMiddleX() + 480 - 95), 250, 96, 48).text("Next").command(this::nextPage).build();

        differentBubbles = Registry.BUBBLES.values().size();
        tickPage();
    }

    private void previousPage() {
        if (helpIndex > 0) {
            SoundInstance focusChangeSFX = new SoundInstance(BubbleBlaster.id("sfx/ui/button/focus_change"), "focusChange");
            focusChangeSFX.setVolume(0.1d);
            focusChangeSFX.play();
        }

        helpIndex = Mth.clamp(helpIndex - 1, 0, differentBubbles - 1);
        tickPage();
    }

    private void nextPage() {
        if (helpIndex < differentBubbles - 1) {
            SoundInstance focusChangeSFX = new SoundInstance(Objects.requireNonNull(getClass().getResource("/assets/bubbles/audio/sfx/ui/button/focus_change.wav")), "focusChange");
            focusChangeSFX.setVolume(0.1d);
            focusChangeSFX.play();
        }

        helpIndex = Mth.clamp(helpIndex + 1, 0, differentBubbles - 1);
        tickPage();
    }

    private void tickPage() {
        bubble = new ArrayList<>(Registry.BUBBLES.values()).get(helpIndex);

        if (helpIndex >= differentBubbles - 1 && nextButton.isValid()) {
            nextButton.destroy();
        } else if (!nextButton.isValid()) {
            nextButton.make();
        }

        if (helpIndex <= 0 && prevButton.isValid()) {
            prevButton.destroy();
        } else if (!prevButton.isValid()) {
            prevButton.make();
        }
    }

    private void changeLanguage() {

    }

    @Override
    public void init() {
        clearWidgets();

        add(exitButton);
        add(prevButton);
        add(nextButton);

        if (!BubbleBlaster.getInstance().isInGame()) {
            return;
        }

        Util.setCursor(BubbleBlaster.getInstance().getDefaultCursor());
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
        Font oldFont = renderer.getFont();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Pause text     //
        ////////////////////////
        renderer.color(Color.argb(0x80ffffff));
        renderer.font(pauseFont);
        GraphicsUtils.drawCenteredString(renderer, Language.translate("bubbles/screen/pause/text"), new Rectangle2D.Double(0, 90, BubbleBlaster.getInstance().getWidth(), renderer.fontMetrics(pauseFont).getHeight()), pauseFont);

        renderer.font(oldFont);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Exit button     //
        /////////////////////////
        exitButton.setText(Language.translate("bubbles/screen/pause/exit"));
        exitButton.render(renderer);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Navigation Buttons & border     //
        /////////////////////////////////////////

        // Navigation buttons.
        nextButton.setText(Language.translate("bubbles/other/next"));
        prevButton.setText(Language.translate("bubbles/other/prev"));

        if (helpIndex > 0) prevButton.render(renderer);
        if (helpIndex < differentBubbles - 1) nextButton.render(renderer);

        // Border
        renderer.color(Color.argb(0x80ffffff));
        renderer.rectLine((int) (BubbleBlaster.getMiddleX() - 480), 300, 960, 300);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Bubble     //
        ////////////////////

        // Bubble name.
        renderer.color(Color.argb(0xc0ffffff));
        renderer.font(bubbleTitleFont);
        renderer.fallbackFont(fallbackTitleFont);
        renderer.text(Language.translate(bubble.getTranslationPath()), (int) BubbleBlaster.getMiddleX() - 470, 332);

        // Bubble icon.
        EnvironmentRenderer.drawBubble(renderer, (int) (BubbleBlaster.getMiddleX() - 470), 350, 122, bubble.getColors());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Info Names     //
        ////////////////////////

        // Set color & font.
        renderer.font(bubbleInfoFont);
        renderer.fallbackFont(fallbackInfoFont);
        renderer.color(Color.argb(0xc0ffffff));

        // Left data.
        renderer.text(minRadius, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 362);
        renderer.text(maxRadius, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 382);
        renderer.text(minSpeed, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 402);
        renderer.text(maxSpeed, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 422);
        renderer.text(defChance, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 442);
        renderer.text(curChance, (int) (BubbleBlaster.getMiddleX() - 326) + 10, 462);

        // Right data.
        renderer.text(defTotPriority, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 322);
        renderer.text(curTotPriority, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 342);
        renderer.text(defPriority, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 362);
        renderer.text(curPriority, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 382);
        renderer.text(scoreMod, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 402);
        renderer.text(attackMod, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 422);
        renderer.text(defenseMod, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 442);
        renderer.text(canSpawn, (int) (BubbleBlaster.getMiddleX() + 72) + 10, 462);

        // Description
        renderer.text(description, (int) BubbleBlaster.getMiddleX() - 470, 502);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //     Values     //
        ////////////////////

        // Set color & font.
        renderer.font(bubbleValueFont);
        renderer.fallbackFont(fallbackValueFont);
        renderer.color(Color.argb(0x80ffffff));

        // Left data.
        renderer.text(Integer.toString(bubble.getMinRadius()), (int) (BubbleBlaster.getMiddleX() - 326) + 200, 362);
        renderer.text(Integer.toString(bubble.getMaxRadius()), (int) (BubbleBlaster.getMiddleX() - 326) + 200, 382);
        renderer.text(Double.toString(Mth.round(bubble.getMinSpeed(), 5)), (int) (BubbleBlaster.getMiddleX() - 326) + 200, 402);
        renderer.text(Double.toString(Mth.round(bubble.getMaxSpeed(), 5)), (int) (BubbleBlaster.getMiddleX() - 326) + 200, 422);
        renderer.text(Mth.round((double) 100 * BubbleSystem.getDefaultPercentageChance(bubble), 5) + "%", (int) (BubbleBlaster.getMiddleX() - 326) + 200, 442);
        renderer.text(Mth.round((double) 100 * BubbleSystem.getPercentageChance(bubble), 5) + "%", (int) (BubbleBlaster.getMiddleX() - 326) + 200, 462);
        renderer.text(compress(BubbleSystem.getDefaultTotalPriority()), (int) (BubbleBlaster.getMiddleX() + 72) + 200, 322);
        renderer.text(compress(BubbleSystem.getTotalPriority()), (int) (BubbleBlaster.getMiddleX() + 72) + 200, 342);
        renderer.text(compress(BubbleSystem.getDefaultPriority(bubble)), (int) (BubbleBlaster.getMiddleX() + 72) + 200, 362);
        renderer.text(compress(BubbleSystem.getPriority(bubble)), (int) (BubbleBlaster.getMiddleX() + 72) + 200, 382);

        // Right data
        if (bubble.isScoreRandom()) {
            renderer.text(random, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 402);
        } else {
            renderer.text(Double.toString(Mth.round(bubble.getScore(), 5)), (int) (BubbleBlaster.getMiddleX() + 72) + 200, 402);
        }
        if (bubble.isAttackRandom()) {
            renderer.text(random, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 422);
        } else {
            renderer.text(Double.toString(Mth.round(bubble.getAttack(), 5)), (int) (BubbleBlaster.getMiddleX() + 72) + 200, 422);
        }
        if (bubble.isDefenseRandom()) {
            renderer.text(random, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 442);
        } else {
            renderer.text(Double.toString(Mth.round(bubble.getDefense(), 5)), (int) (BubbleBlaster.getMiddleX() + 72) + 200, 442);
        }
        renderer.text(bubble.canSpawn(loadedGame.getEnvironment()) ? boolTrue : boolFalse, (int) (BubbleBlaster.getMiddleX() + 72) + 200, 462);

        // Description
        renderer.wrappedText(Language.translate(bubble.getDescriptionTranslationPath()).replaceAll("\\\\n", "\n"), (int) BubbleBlaster.getMiddleX() - 470, 522, 940);

        renderer.font(oldFont);
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
