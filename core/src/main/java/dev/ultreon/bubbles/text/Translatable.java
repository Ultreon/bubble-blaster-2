package dev.ultreon.bubbles.text;

import dev.ultreon.bubbles.lang.Language;

public interface Translatable {
    String getTranslationPath();

    default MutableText getTranslation() {
        return TextObject.translation(this.getTranslationPath());
    }

    default String getTranslationText() {
        return Language.translate(this.getTranslationPath());
    }
}
