package com.ultreon.libs.text.v1;

public class LiteralText extends MutableText {
    private final String text;

    LiteralText(String text) {
        this.text = text;
    }

    @Override
    protected String createString() {
        return this.text;
    }
}
