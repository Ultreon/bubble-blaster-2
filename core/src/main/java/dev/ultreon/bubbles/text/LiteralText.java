package dev.ultreon.bubbles.text;

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
