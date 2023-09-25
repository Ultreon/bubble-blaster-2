package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.libs.text.v0.TextObject;
import com.ultreon.libs.translations.v0.Language;
import com.ultreon.libs.translations.v0.LanguageManager;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("FieldCanBeLocal")
public final class LanguageScreen extends Screen {
    private static LanguageScreen INSTANCE;
    private final List<Language> languages;
    private ObjectList<Language> languageList;
    private OptionsButton cancelButton;
    private Screen backScreen;
    private Locale oldLanguage;
    private OptionsButton okButton;

    public LanguageScreen(Screen backScreen) {
        LanguageScreen.INSTANCE = this;

        this.backScreen = backScreen;
        this.languages = LanguageManager.INSTANCE.getLanguages();
        this.languages.sort((o1, o2) -> o1.getLocale().getDisplayLanguage(Locale.ENGLISH).compareToIgnoreCase(o2.getLocale().getDisplayLanguage(Locale.ENGLISH)));
    }

    @Override
    public void init() {
        this.clearWidgets();

        var calcWidth = calculateWidth();

        this.oldLanguage = GameSettings.instance().getLanguageLocale();

        this.languageList = add(new ObjectList<>(this.languages, 60, 2, (width - calcWidth) / 2, 0, calcWidth, this.height - 60));
        this.languageList.setSelectable(true);
        this.languageList.setEntryRenderer(this::renderEntry);
        this.languageList.addSelectHandler(entry -> {
            GameSettings instance = GameSettings.instance();
            instance.setLanguage(entry.value.getLocale());
        });

        this.okButton = add(new OptionsButton.Builder().bounds((width - calcWidth) / 2, height - 50, calcWidth / 2 - 5, 40).text(TextObject.translation("bubbles/other/ok")).build());
        this.okButton.setCommand(this::apply);

        this.cancelButton = add(new OptionsButton.Builder().bounds((width / 2) + 5, height - 50, calcWidth / 2 - 5, 40).text(TextObject.translation("bubbles/other/cancel")).build());
        this.cancelButton.setCommand(this::cancel);
    }

    private void renderEntry(Renderer renderer, int width, int height, Language entry, boolean selected, boolean hovered) {
        Locale locale = entry.getLocale();
        String language = locale.getDisplayLanguage(Locale.ENGLISH);

        fill(renderer, 0, 0, width, height, hovered ? 0x40ffffff : 0x20ffffff);

        if (selected) {
            renderer.drawEffectBox(10, 10, width - 20, height - 20, new Insets(2, 2, 2, 2));
        }

        renderer.setColor(0xc0ffffff);
        renderer.drawText(Fonts.SANS_BOLD_20.get(), language, 20, 20);
        renderer.setColor(0x60ffffff);
//        renderer.text(StringUtils.createFallbackString(displayLanguage, font.getGdxFont(14, Thickness.BOLD, FontStyle.PLAIN), font.getGdxFont(14, Thickness.BOLD, FontStyle.PLAIN, locale.getLanguage())), 20 + font.width(14, language) + 40, 20 + font.height(14));
    }

    private int calculateWidth() {
        return Math.min(width - 50, 500);
    }

    private void cancel() {
        GameSettings.instance().setLanguage(oldLanguage);
        game.showScreen(backScreen);
    }

    private void apply() {
        game.showScreen(backScreen);
    }

    public static LanguageScreen instance() {
        return INSTANCE;
    }

    @Override
    public boolean onClose(Screen to) {
        cancelButton.destroy();

        if (to == backScreen) {
            backScreen = null;
        }
        return super.onClose(to);
    }
}
