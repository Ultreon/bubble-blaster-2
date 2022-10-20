package com.ultreon.bubbles.common.text.components;

import com.ultreon.bubbles.common.text.translation.Language;

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
