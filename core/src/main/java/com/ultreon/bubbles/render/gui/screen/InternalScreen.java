package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.libs.text.v1.TextObject;

public abstract sealed class InternalScreen extends Screen permits LoadScreen, SplashScreen {
    public InternalScreen() {
        super();
    }

    public InternalScreen(TextObject title) {
        super(title);
    }

    public InternalScreen(Screen backScreen) {
        super(backScreen);
    }

    public InternalScreen(TextObject title, Screen backScreen) {
        super(title, backScreen);
    }
}
