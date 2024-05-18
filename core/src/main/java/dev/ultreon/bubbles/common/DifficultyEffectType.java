package dev.ultreon.bubbles.common;

import com.google.common.base.CaseFormat;
import dev.ultreon.libs.text.v1.Translatable;

public enum DifficultyEffectType implements Translatable {
    BUBBLE_SPEED(true, false),
    LOCAL(false, true),
    BOTH(true, true);

    private final boolean speed;
    private final boolean local;

    DifficultyEffectType(boolean speed, boolean local) {
        this.speed = speed;
        this.local = local;
    }

    public boolean isSpeed() {
        return this.speed;
    }

    public boolean isLocal() {
        return this.local;
    }

    @Override
    public String getTranslationPath() {
        return "bubbleblaster.misc.difficultyEffect." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    }
}
