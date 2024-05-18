package dev.ultreon.bubbles.render.gui.screen.options;

import dev.ultreon.bubbles.BubbleBlasterConfig;
import dev.ultreon.bubbles.init.Fonts;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Insets;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.bubbles.render.gui.widget.Button;
import dev.ultreon.bubbles.render.gui.widget.ObjectList;
import dev.ultreon.bubbles.text.Translations;
import dev.ultreon.libs.translations.v1.Language;
import dev.ultreon.libs.translations.v1.LanguageManager;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("FieldCanBeLocal")
public final class LanguageScreen extends Screen {
    private static LanguageScreen INSTANCE;
    private final List<Language> languages;
    private final Locale oldLanguage;
    private ObjectList<Language> languageList;
    private Button cancelButton;
    private Button saveButton;

    public LanguageScreen() {
        super();
        LanguageScreen.INSTANCE = this;

        this.languages = LanguageManager.INSTANCE.getLanguages();
        this.languages.sort((o1, o2) -> o1.getLocale().getDisplayLanguage(Locale.ENGLISH).compareToIgnoreCase(o2.getLocale().getDisplayLanguage(Locale.ENGLISH)));

        this.oldLanguage = LanguageManager.getCurrentLanguage();
    }

    @Override
    public void init() {
        this.clearWidgets();

        var calcWidth = this.calculateWidth();

        this.languageList = this.add(new ObjectList<>(this.languages, 40, 2, (this.width - calcWidth) / 2, 0, calcWidth, this.height - 60));
        this.languageList.setSelectable(true);
        this.languageList.setEntryRenderer(this::renderEntry);
        this.languageList.addSelectHandler(entry -> LanguageManager.setCurrentLanguage(entry.value.getLocale()));

        this.saveButton = this.add(Button.builder().bounds(this.width / 2 + 5, this.height - 50, calcWidth / 2 - 5, 40).text(Translations.SAVE).build());
        this.saveButton.setCommand(this::save);

        this.cancelButton = this.add(Button.builder().bounds((this.width - calcWidth) / 2, this.height - 50, calcWidth / 2 - 5, 40).text(Translations.CANCEL).build());
        this.cancelButton.setCommand(this::cancel);
    }

    private void renderEntry(Renderer renderer, float width, float height, float y, Language entry, boolean selected, boolean hovered) {
        var locale = entry.getLocale();
        var language = locale.getDisplayLanguage(Locale.ENGLISH);
        var country = locale.getDisplayCountry(Locale.ENGLISH);

        var x = this.languageList.getX();

        renderer.fill(x, y, width, height, Color.WHITE.withAlpha(hovered ? 0x40 : 0x20));

        if (selected)
            renderer.drawEffectBox(x, y, width, height, new Insets(0, 0, 4, 0));

        renderer.drawTextCenter(Fonts.SANS_PARAGRAPH.get(), language + " (" + country + ")", x + width / 2, y + (height - 4) / 2, Color.WHITE.withAlpha(0xc0));
    }

    private int calculateWidth() {
        return Math.min(this.width - 50, 500);
    }

    private void cancel() {
        LanguageManager.setCurrentLanguage(this.oldLanguage);
        this.back();
    }

    private void save() {
        var selected = this.languageList.getSelected();
        if (selected == null) {
            this.back();
            return;
        }
        BubbleBlasterConfig.LANGUAGE.set(selected.value.getLocale().toLanguageTag());
        BubbleBlasterConfig.save();
        this.back();
    }

    public static LanguageScreen instance() {
        return INSTANCE;
    }
}
