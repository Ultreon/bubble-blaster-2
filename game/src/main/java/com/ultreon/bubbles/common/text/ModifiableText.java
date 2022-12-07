package com.ultreon.bubbles.common.text;

public class ModifiableText extends LiteralText {
    private String text;

    public ModifiableText(String text) {
        super(text);

        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
