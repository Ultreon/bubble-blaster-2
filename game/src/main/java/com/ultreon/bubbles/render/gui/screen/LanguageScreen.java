package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.common.text.TranslationText;
import com.ultreon.bubbles.common.text.translation.Language;
import com.ultreon.bubbles.common.text.translation.LanguageManager;
import com.ultreon.bubbles.event.v1.SubscribeEvent;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.ValueAnimator;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.Util;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

@SuppressWarnings("FieldCanBeLocal")
public final class LanguageScreen extends Screen {
    private static LanguageScreen INSTANCE;
    private int languageIndex = 1;
    private final OptionsButton button1 = new OptionsButton.Builder().bounds(0, 0, 512, 48).build();
    private final OptionsButton button2 = new OptionsButton.Builder().bounds(0, 0, 512, 48).build();
    private final OptionsButton button3 = new OptionsButton.Builder().bounds(0, 0, 512, 48).build();
    private final OptionsButton button4 = new OptionsButton.Builder().bounds(0, 0, 512, 48).build();
    private final OptionsButton button5 = new OptionsButton.Builder().bounds(0, 0, 512, 48).build();
    private final OptionsButton button6 = new OptionsButton.Builder().bounds(0, 0, 512, 48).build();
    private final OptionsButton prev = new OptionsButton.Builder().bounds(0, 0, 64, 298).text(new TranslationText("bubbles/other/prev")).build();
    private final OptionsButton next = new OptionsButton.Builder().bounds(0, 0, 64, 298).text(new TranslationText("bubbles/other/next")).build();

    private final OptionsButton cancelButton = new OptionsButton.Builder().bounds(0, 0, 644, 48).text(new TranslationText("bubbles/other/cancel")).build();
    private static final TreeMap<String, Locale> nameLocaleMap = new TreeMap<>();
    private Screen backScene;
    private int deltaIndex;
    private ValueAnimator valueAnimator1;
    private ValueAnimator valueAnimator2;
    private int deltaPage;

    private class LanguageLoader {
        @SubscribeEvent
        public void onLoadComplete() {
            LanguageScreen.this.onPostInitialize();
        }
    }

    public LanguageScreen(Screen backScene) {
        LanguageScreen.INSTANCE = this;

        this.backScene = backScene;

        this.button1.setCommand(this::cmdButton1);
        this.button2.setCommand(this::cmdButton2);
        this.button3.setCommand(this::cmdButton3);
        this.button4.setCommand(this::cmdButton4);
        this.button5.setCommand(this::cmdButton5);
        this.button6.setCommand(this::cmdButton6);
        this.prev.setCommand(this::prevPage);
        this.next.setCommand(this::nextPage);
        this.cancelButton.setCommand(this::back);
    }

    private void back() {
        Objects.requireNonNull(Util.getSceneManager()).displayScreen(backScene);
    }

    public static void onPostInitialize() {
        for (Locale locale : LanguageManager.INSTANCE.getLocales()) {
            String name;
            if (!Objects.equals(locale.getDisplayCountry(), ""))
                name = locale.getDisplayLanguage() + " (" + locale.getDisplayCountry() + ")";
            else {
                name = locale.getDisplayLanguage();
            }

            nameLocaleMap.put(name, locale);
        }
    }

    private void cmdButton6() {
        setLanguage(languageIndex + 5);
    }

    private void cmdButton5() {
        setLanguage(languageIndex + 4);
    }

    private void cmdButton4() {
        setLanguage(languageIndex + 3);
    }

    private void cmdButton3() {
        setLanguage(languageIndex + 2);
    }

    private void cmdButton2() {
        setLanguage(languageIndex + 1);
    }

    private void cmdButton1() {
        setLanguage(languageIndex);
    }

    private void setLanguage(int languageIndex) {
        Locale locale = new ArrayList<>(nameLocaleMap.values()).get(languageIndex);
        GameSettings settings = GameSettings.instance();

        settings.setLanguage(locale);

        back();
    }

    private void nextPage() {
        if (valueAnimator1 == null && valueAnimator2 == null) {
//            languageIndex += 6;

            deltaIndex = 6;
            deltaPage = 1;

            valueAnimator1 = new ValueAnimator(0, 512 * -deltaPage, 0.05);
            valueAnimator1.start();
        }
    }

    private void prevPage() {
        if (valueAnimator1 == null && valueAnimator2 == null) {
//            languageIndex -= 6;

            deltaIndex = -6;
            deltaPage = -1;

            valueAnimator1 = new ValueAnimator(0, 512 * -deltaPage, 0.05);
            valueAnimator1.start();
        }
    }

    public static LanguageScreen instance() {
        return INSTANCE;
    }

    @Override
    public void init() {
        button1.make();
        button2.make();
        button3.make();
        button4.make();
        button5.make();
        button6.make();
        next.make();
        prev.make();

        cancelButton.make();
    }

    @Override
    public boolean onClose(Screen to) {
        button1.destroy();
        button2.destroy();
        button3.destroy();
        button4.destroy();
        button5.destroy();
        button6.destroy();
        next.destroy();
        prev.destroy();

        cancelButton.destroy();

        if (to == backScene) {
            backScene = null;
        }
        return super.onClose(to);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        prev.setX((int) BubbleBlaster.getMiddleX() - 322);
        prev.setY((int) BubbleBlaster.getMiddleY() - 149);
        button1.setX((int) BubbleBlaster.getMiddleX() - 256);
        button2.setX((int) BubbleBlaster.getMiddleX() - 256);
        button3.setX((int) BubbleBlaster.getMiddleX() - 256);
        button4.setX((int) BubbleBlaster.getMiddleX() - 256);
        button5.setX((int) BubbleBlaster.getMiddleX() - 256);
        button6.setX((int) BubbleBlaster.getMiddleX() - 256);
        button1.setY((int) BubbleBlaster.getMiddleY() - 149);
        button2.setY((int) BubbleBlaster.getMiddleY() - 99);
        button3.setY((int) BubbleBlaster.getMiddleY() - 49);
        button4.setY((int) BubbleBlaster.getMiddleY() + 1);
        button5.setY((int) BubbleBlaster.getMiddleY() + 51);
        button6.setY((int) BubbleBlaster.getMiddleY() + 101);
        next.setX((int) BubbleBlaster.getMiddleX() + 258);
        next.setY((int) BubbleBlaster.getMiddleY() - 149);

        cancelButton.setX((int) BubbleBlaster.getMiddleX() - 322);
        cancelButton.setY((int) BubbleBlaster.getMiddleY() + 151);

//        if (evt.getPriority() == RenderEventPriority.BACKGROUND) {
//        }

//        if (evt.getPriority() == RenderEventPriority.FOREGROUND) {
//        }

        renderBackground(renderer);

        renderForeground(game, renderer);
    }

    private void renderForeground(BubbleBlaster game, Renderer renderer) {
        Locale loc1 = null;
        Locale loc2 = null;
        Locale loc3 = null;
        Locale loc4 = null;
        Locale loc5 = null;
        Locale loc6 = null;
        try {
            loc1 = new ArrayList<>(nameLocaleMap.values()).get(languageIndex);
            button1.setText(loc1.getDisplayLanguage(loc1) + " (" + loc1.getDisplayLanguage() + ")");
        } catch (IndexOutOfBoundsException ignored) {

        }
        try {
            loc2 = new ArrayList<>(nameLocaleMap.values()).get(languageIndex + 1);
            button2.setText(loc2.getDisplayLanguage(loc2) + " (" + loc2.getDisplayLanguage() + ")");
        } catch (IndexOutOfBoundsException ignored) {

        }
        try {
            loc3 = new ArrayList<>(nameLocaleMap.values()).get(languageIndex + 2);
            button3.setText(loc3.getDisplayLanguage(loc3) + " (" + loc3.getDisplayLanguage() + ")");
        } catch (IndexOutOfBoundsException ignored) {

        }

        try {
            loc4 = new ArrayList<>(nameLocaleMap.values()).get(languageIndex + 3);
            button4.setText(loc4.getDisplayLanguage(loc4) + " (" + loc4.getDisplayLanguage() + ")");
        } catch (IndexOutOfBoundsException ignored) {

        }
        try {
            loc5 = new ArrayList<>(nameLocaleMap.values()).get(languageIndex + 4);
            button5.setText(loc5.getDisplayLanguage(loc5) + " (" + loc5.getDisplayLanguage() + ")");
        } catch (IndexOutOfBoundsException ignored) {

        }
        try {
            loc6 = new ArrayList<>(nameLocaleMap.values()).get(languageIndex + 5);
            button6.setText(loc6.getDisplayLanguage(loc6) + " (" + loc6.getDisplayLanguage() + ")");
        } catch (IndexOutOfBoundsException ignored) {

        }

        next.setText(Language.translate("bubbles/other/next"));
        prev.setText(Language.translate("bubbles/other/prev"));

        cancelButton.setText(Language.translate("bubbles/other/cancel"));

        Renderer buttonsRender = renderer.subInstance((int) BubbleBlaster.getMiddleX() - 256, (int) BubbleBlaster.getMiddleY() - 149, 512, 300);
        Renderer animRender;
        if (valueAnimator1 != null) {
            if (valueAnimator1.isEnded()) {
                languageIndex += deltaIndex;
                valueAnimator1 = null;

                valueAnimator2 = new ValueAnimator(512 * deltaPage, 0, 0.05);
                valueAnimator2.start();

                int x = (int) valueAnimator2.animate();
                animRender = buttonsRender.subInstance(x, 0, 512, 300);
            } else {
                int x = (int) valueAnimator1.animate();
                animRender = buttonsRender.subInstance(x, 0, 512, 300);
            }
        } else {
            if (valueAnimator2 != null) {
                if (valueAnimator2.isEnded()) {
                    animRender = buttonsRender.subInstance(0, 0, 512, 300);
                    valueAnimator2 = null;
                } else {
                    int x = (int) valueAnimator2.animate();
                    animRender = buttonsRender.subInstance(x, 0, 512, 300);
                }
            } else {
                animRender = buttonsRender.subInstance(0, 0, 512, 300);
            }
        }


        if (loc1 != null) button1.render(animRender);
        if (loc2 != null) button2.render(animRender);
        if (loc3 != null) button3.render(animRender);
        if (loc4 != null) button4.render(animRender);
        if (loc5 != null) button5.render(animRender);
        if (loc6 != null) button6.render(animRender);

        animRender.dispose();
        buttonsRender.dispose();

        next.render(renderer);
        prev.render(renderer);

        cancelButton.render(renderer);
    }

    public void renderBackground(Renderer renderer) {
        renderer.color(Color.rgb(0x606060));
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
    }
}
