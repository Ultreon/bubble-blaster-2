package com.ultreon.bubbles.common.text;

public abstract class TextObject {
    public static final TextObject EMPTY = new TextObject() {
        @Override
        public String getText() {
            return "";
        }
    };

    public abstract String getText();
}
