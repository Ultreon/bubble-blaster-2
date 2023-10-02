package com.ultreon.bubbles;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ultreon.libs.commons.v0.Identifier;

public class FontUtils {
    public static BitmapFont createBitmapFont(Identifier id, int size) {
        return BubbleBlaster.getInstance().loadFont(id, size);
    }
}
