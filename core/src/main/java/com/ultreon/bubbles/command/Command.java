package com.ultreon.bubbles.command;

import java.util.Objects;

public final class Command {
    private final String name;

    public Command(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Command) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "Command[" +
                "name=" + this.name + ']';
    }

}
