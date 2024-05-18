package dev.ultreon.bubbles;

import java.time.Instant;
import java.util.Objects;

public final class ChatMessage {
    private final String text;
    private final Instant created;
    private final boolean system;

    public ChatMessage(String text, Instant created, boolean system) {
        this.text = text;
        this.created = created;
        this.system = system;
    }

    public ChatMessage(String message, boolean system) {
        this(message, Instant.now(), system);
    }

    public String text() {
        return this.text;
    }

    public Instant created() {
        return this.created;
    }

    public boolean system() {
        return this.system;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ChatMessage) obj;
        return Objects.equals(this.text, that.text) &&
                Objects.equals(this.created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.text, this.created);
    }

    @Override
    public String toString() {
        return "ChatMessage[" +
                "text=" + this.text + ", " +
                "created=" + this.created + ']';
    }
}
