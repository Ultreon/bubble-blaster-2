package com.ultreon.bubbles.input;

import com.badlogic.gdx.Gdx;
import com.ultreon.libs.commons.v0.tuple.Pair;

public class KeyBinding {
    private final int defaultCode;
    private final Type defaultType;
    private Type type;
    private int code;

    public KeyBinding(int code, Type type) {
        this.defaultCode = code;
        this.code = code;
        this.defaultType = type;
        this.type = type;
    }

    public int getDefaultCode() {
        return this.defaultCode;
    }

    public Type getDefaultType() {
        return this.defaultType;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void set(Type type, int code) {
        this.type = type;
        this.code = code;
    }

    public Pair<Type, Integer> get() {
        return new Pair<>(this.type, this.code);
    }

    public boolean isPressed() {
        return switch (this.type) {
            case MOUSE -> Gdx.input.isButtonPressed(this.code);
            case KEYBOARD -> Gdx.input.isKeyPressed(this.code);
        };
    }

    public boolean isJustPressed() {
        return switch (this.type) {
            case MOUSE -> Gdx.input.isButtonJustPressed(this.code);
            case KEYBOARD -> Gdx.input.isKeyJustPressed(this.code);
        };
    }

    public void reset() {
        this.code = this.defaultCode;
        this.type = this.defaultType;
    }

    public enum Type {
        MOUSE, KEYBOARD
    }
}
