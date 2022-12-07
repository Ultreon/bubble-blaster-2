package com.ultreon.bubbles.common.text;

public class LiteralText extends TextObject {
    private final String text;

    public LiteralText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
