package com.ultreon.bubbles.render.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ultreon.libs.commons.v0.Identifier;

import java.util.UUID;

public class SystemFont extends Font {
    public SystemFont(String name) {
        super();
        FontInfo.Builder builder = new FontInfo.Builder(new Identifier("java", UUID.nameUUIDFromBytes(name.getBytes()).toString().replaceAll("-", "")));
        builder.set(Thickness.REGULAR, FontStyle.PLAIN, new BitmapFont());
        builder.set(Thickness.REGULAR, FontStyle.ITALIC, new BitmapFont());
        builder.set(Thickness.BOLD, FontStyle.PLAIN, new BitmapFont());
        builder.set(Thickness.BOLD, FontStyle.ITALIC, new BitmapFont());
        this.info = builder.build();
    }

    public SystemFont(FontInfo info) {
        super();
        this.info = info;
    }

    @Override
    @Deprecated
    public void register() {

    }

    public void alternative(String langCode, FontInfo font) {
        this.alternatives.put(langCode, font);
    }
}
