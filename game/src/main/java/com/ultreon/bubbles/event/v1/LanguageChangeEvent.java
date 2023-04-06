package com.ultreon.bubbles.event.v1;

import com.ultreon.commons.lang.ICancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@Deprecated
public class LanguageChangeEvent extends Event implements ICancellable {
    private final Locale from;
    private final Locale to;

    public LanguageChangeEvent(Locale from, Locale to) {
        this.from = from;
        this.to = to;
    }

    @Nullable
    public Locale getFrom() {
        return from;
    }

    @NotNull
    public Locale getTo() {
        return to;
    }
}
