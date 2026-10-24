package dev.ultreon.bubbles.random.valuesource;

import dev.ultreon.bubbles.text.Translatable;

public interface ValueSource extends Translatable {
    double getValue();

    @Override
    default String getTranslationPath() {
        return "bubbleblaster.misc.unknown";
    }
}
