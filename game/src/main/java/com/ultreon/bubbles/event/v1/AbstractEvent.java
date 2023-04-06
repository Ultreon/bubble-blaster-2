package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.annotation.Cancelable;

@Deprecated
public abstract class AbstractEvent {
    @Deprecated()
    private boolean cancelled;

    @Deprecated()
    public final void cancel() {
        this.cancelled = true;
    }

    @Deprecated()
    public final boolean isCancelled() {
        return isCancelable() && cancelled;
    }

    @Deprecated()
    public final boolean isCancelable() {
        return getClass().isAnnotationPresent(Cancelable.class);
    }
}
