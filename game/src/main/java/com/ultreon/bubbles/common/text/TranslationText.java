package com.ultreon.bubbles.common.text;

import com.ultreon.libs.translations.v0.Language;

public class TranslationText extends TextObject {
    private final String path;
    private final Object[] args;

    public TranslationText(String path, Object... args) {
        this.path = path;
        this.args = args;
    }

    public String getPath() {
        return path;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String getText() {
        return Language.translate(path, args);
    }
}
