package dev.ultreon.bubbles.render.gui.style;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StateBundle<T> {
    protected T active;
    protected T hover;
    protected T normal;
    protected T pressed;

    public StateBundle(@NotNull T hover, @NotNull T normal, @NotNull T pressed) {
        this(hover, normal, pressed, null);
    }

    public StateBundle(@NotNull T hover, @NotNull T normal, @NotNull T pressed, @Nullable T active) {
        this.hover = hover;
        this.normal = normal;
        this.pressed = pressed;
        this.active = active;
    }


    @Nullable
    public T getActive() {
        return this.active;
    }

    public void setActive(@Nullable T active) {
        this.active = active;
    }

    @NotNull
    public T getHover() {
        return this.hover;
    }

    public void setHover(@NotNull T hover) {
        this.hover = hover;
    }

    @NotNull
    public T getNormal() {
        return this.normal;
    }

    public void setNormal(@NotNull T normal) {
        this.normal = normal;
    }

    @NotNull
    public T getPressed() {
        return this.pressed;
    }

    public void setPressed(@NotNull T pressed) {
        this.pressed = pressed;
    }
}
