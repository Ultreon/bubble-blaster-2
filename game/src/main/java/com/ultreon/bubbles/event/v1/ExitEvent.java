package com.ultreon.bubbles.event.v1;

import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated
public class ExitEvent extends Event {
    private final @Nullable Reason reason;
    private final @Nullable String message;

    public ExitEvent() {
        this(null, null);
    }

    public ExitEvent(@Nullable String message) {
        this(null, message);
    }

    public ExitEvent(@Nullable Reason reason) {
        this(reason, null);
    }

    public ExitEvent(@Nullable Reason reason, @Nullable String message) {
        this.reason = reason;
        this.message = message;
    }

    @Nullable
    public Reason getReason() {
        return reason;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    private enum Reason {
        USER_EXIT(0),
        FATAL_ERROR(1);

        private final int code;

        Reason(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
