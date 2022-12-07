package com.ultreon.bubbles.common;

import java.util.Objects;

public abstract class Registrable implements IRegistrable {
    private Identifier id = null;

    public Identifier id() {
        return id;
    }

    public void setId(Identifier name) {
        this.id = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registrable that = (Registrable) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
