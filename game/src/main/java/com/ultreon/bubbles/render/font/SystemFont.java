package com.ultreon.bubbles.render.font;

public class SystemFont extends Font {
    public SystemFont(String name) {
        super();
        FontInfo.Builder builder = new FontInfo.Builder();
        builder.set(Thickness.REGULAR, FontStyle.PLAIN, new java.awt.Font(name, java.awt.Font.PLAIN, 0));
        builder.set(Thickness.REGULAR, FontStyle.ITALIC, new java.awt.Font(name, java.awt.Font.ITALIC, 0));
        builder.set(Thickness.BOLD, FontStyle.PLAIN, new java.awt.Font(name, java.awt.Font.BOLD, 0));
        builder.set(Thickness.BOLD, FontStyle.ITALIC, new java.awt.Font(name, java.awt.Font.BOLD + java.awt.Font.ITALIC, 0));
        this.info = builder.build();
    }

    public SystemFont(FontInfo name) {
        super();
        this.info = name;
    }

    @Override
    @Deprecated
    public void register() {

    }
}
