package com.ultreon.bubbles.common.text.components;

import com.ultreon.libs.translations.v0.Language;

public class TranslatableText extends Text {
    private final String id;
    private final Object[] args;

    public TranslatableText(String id, Object... args) {
        this.id = id;
        this.args = args;
    }

    @Override
    public String getString() {
        return Language.translate(id, args);
    }
}
