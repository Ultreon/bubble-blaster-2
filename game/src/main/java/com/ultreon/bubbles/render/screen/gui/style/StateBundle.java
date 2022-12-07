package com.ultreon.bubbles.render.screen.gui.style;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class StateBundle<T> {
    protected T active;
    protected T hover;
    protected T normal;
    protected T pressed;

    public StateBundle(@NonNull T hover, @NonNull T normal, @NonNull T pressed) {
        this(hover, normal, pressed, null);
    }

    public StateBundle(@NonNull T hover, @NonNull T normal, @NonNull T pressed, @Nullable T active) {
        this.hover = hover;
        this.normal = normal;
        this.pressed = pressed;
        this.active = active;
    }


    @Nullable
    public T getActive() {
        return active;
    }

    public void setActive(@Nullable T active) {
        this.active = active;
    }

    @NonNull
    public T getHover() {
        return hover;
    }

    public void setHover(@NonNull T hover) {
        this.hover = hover;
    }

    @NonNull
    public T getNormal() {
        return normal;
    }

    public void setNormal(@NonNull T normal) {
        this.normal = normal;
    }

    @NonNull
    public T getPressed() {
        return pressed;
    }

    public void setPressed(@NonNull T pressed) {
        this.pressed = pressed;
    }
}
