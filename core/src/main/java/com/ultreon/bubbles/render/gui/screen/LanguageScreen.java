package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.text.Translations;
import com.ultreon.libs.translations.v1.Language;
import com.ultreon.libs.translations.v1.LanguageManager;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("FieldCanBeLocal")
public final class LanguageScreen extends Screen {
    private static LanguageScreen INSTANCE;
    private final List<Language> languages;
    private ObjectList<Language> languageList;
    private Button cancelButton;
    private Screen backScreen;
    private Locale oldLanguage;
    private Button okButton;

    public LanguageScreen(Screen backScreen) {
        LanguageScreen.INSTANCE = this;

        this.backScreen = backScreen;
        this.languages = LanguageManager.INSTANCE.getLanguages();
        this.languages.sort((o1, o2) -> o1.getLocale().getDisplayLanguage(Locale.ENGLISH).compareToIgnoreCase(o2.getLocale().getDisplayLanguage(Locale.ENGLISH)));
    }

    @Override
    public void init() {
        this.clearWidgets();

        var calcWidth = this.calculateWidth();

        this.oldLanguage = GameSettings.instance().getLanguageLocale();

        this.languageList = this.add(new ObjectList<>(this.languages, 60, 2, (this.width - calcWidth) / 2, 0, calcWidth, this.height - 60));
        this.languageList.setSelectable(true);
        this.languageList.setEntryRenderer(this::renderEntry);
        this.languageList.addSelectHandler(entry -> {
            GameSettings instance = GameSettings.instance();
            instance.setLanguage(entry.value.getLocale());
            GameSettings.save();
        });

        this.okButton = this.add(Button.builder().bounds((this.width - calcWidth) / 2, this.height - 50, calcWidth / 2 - 5, 40).text(Translations.OK).build());
        this.okButton.setCommand(this::apply);

        this.cancelButton = this.add(Button.builder().bounds((this.width / 2) + 5, this.height - 50, calcWidth / 2 - 5, 40).text(Translations.CANCEL).build());
        this.cancelButton.setCommand(this::cancel);
    }

    private void renderEntry(Renderer renderer, int width, int height, float y, Language entry, boolean selected, boolean hovered) {
        Locale locale = entry.getLocale();
        String language = locale.getDisplayLanguage(Locale.ENGLISH);

        int x = this.languageList.getX();

        renderer.fill(x, y, width, height, Color.WHITE.withAlpha(hovered ? 0x40 : 0x20));

        if (selected) {
            renderer.drawEffectBox(x + 10, y + 10, width - 20, height - 20, new Insets(2, 2, 2, 2));
        }

        renderer.drawText(Fonts.SANS_BOLD_20.get(), language, x + 20, y + 20, Color.WHITE.withAlpha(0xc0));
//        renderer.setColor(0x60ffffff);
//        renderer.text(StringUtils.createFallbackString(displayLanguage, font.getGdxFont(14, Thickness.BOLD, FontStyle.PLAIN), font.getGdxFont(14, Thickness.BOLD, FontStyle.PLAIN, locale.getLanguage())), 20 + font.width(14, language) + 40, 20 + font.height(14));
    }

    private int calculateWidth() {
        return Math.min(this.width - 50, 500);
    }

    private void cancel() {
        GameSettings.instance().setLanguage(this.oldLanguage);
        GameSettings.save();
        this.game.showScreen(this.backScreen);
    }

    private void apply() {
        this.game.showScreen(this.backScreen);
    }

    public static LanguageScreen instance() {
        return INSTANCE;
    }
}
