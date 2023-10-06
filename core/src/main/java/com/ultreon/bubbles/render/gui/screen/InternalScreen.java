package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.text.v1.TextObject;

public abstract class InternalScreen extends Screen {
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

    @Override
    public void renderCloseButton(Renderer renderer, int mouseX, int mouseY) {
        
    }
}
