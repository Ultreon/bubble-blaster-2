package dev.ultreon.bubbles.input;

import com.badlogic.gdx.Gdx;
import dev.ultreon.libs.commons.v0.tuple.Pair;

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
        switch (this.type) {
            case MOUSE:
                return Gdx.input.isButtonPressed(this.code);
            case KEYBOARD:
                return Gdx.input.isKeyPressed(this.code);
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isJustPressed() {
        switch (this.type) {
            case MOUSE:
                return Gdx.input.isButtonJustPressed(this.code);
            case KEYBOARD:
                return Gdx.input.isKeyJustPressed(this.code);
            default:
                throw new IllegalArgumentException();
        }
    }

    public void reset() {
        this.code = this.defaultCode;
        this.type = this.defaultType;
    }

    public enum Type {
        MOUSE, KEYBOARD
    }
}
