package com.ultreon.bubbles.random.valuesource;

import com.ultreon.libs.text.v1.Translatable;

public interface ValueSource extends Translatable {
    double getValue();

    @Override
    default String getTranslationPath() {
        return "bubbleblaster.misc.unknown";
    }
}
