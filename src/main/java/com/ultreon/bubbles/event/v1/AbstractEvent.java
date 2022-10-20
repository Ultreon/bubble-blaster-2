package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.annotation.Cancelable;

@Deprecated
public abstract class AbstractEvent {
    private boolean cancelled;

    public final void cancel() {
        this.cancelled = true;
    }

    public final boolean isCancelled() {
        return isCancelable() && cancelled;
    }

    public final boolean isCancelable() {
        return getClass().isAnnotationPresent(Cancelable.class);
    }
}
