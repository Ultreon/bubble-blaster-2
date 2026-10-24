package dev.ultreon.bubbles;

import dev.ultreon.bubbles.text.Translatable;

public enum Axis2D implements Translatable {
    HORIZONTAL("horizontal"),
    VERTICAL("vertical");

    private final String name;

    Axis2D(String name) {
        this.name = name;
    }

    @Override
    public String getTranslationPath() {
        return "bubbles.misc." + this.name;
    }
}
