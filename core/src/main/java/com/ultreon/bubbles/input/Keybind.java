package com.ultreon.bubbles.input;

import com.ultreon.bubbles.core.input.KeyboardInput;

public class Keybind {
    private final int defaultKey;
    private int keyCode;

    /**
     * @param defaultKey the default keycode for binding.
     */
    public Keybind(int defaultKey) {
        this.defaultKey = defaultKey;
        this.keyCode = defaultKey;
    }

    public int getDefaultKey() {
        return defaultKey;
    }

    public boolean isDown() {
        return KeyboardInput.isDown(keyCode);
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
}
