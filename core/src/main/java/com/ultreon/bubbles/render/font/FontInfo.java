package com.ultreon.bubbles.render.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.ultreon.libs.commons.v0.Identifier;

import java.util.function.BiFunction;

public class FontInfo {
    private final Identifier id;
    private final BiFunction<Thickness, FontStyle, BitmapFont> fontGetter;

    private FontInfo(Identifier id, BiFunction<Thickness, FontStyle, BitmapFont> fontGetter) {
        this.id = id;
        this.fontGetter = fontGetter;
    }

    public Identifier getId() {
        return id;
    }

    public BitmapFont getFont(Thickness thickness, FontStyle style) {
        return this.fontGetter.apply(thickness, style);
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    public static class Builder {
        private final Table<Thickness, FontStyle, BitmapFont> fontTable = HashBasedTable.create();
        private final Identifier id;

        public Builder(Identifier id) {
            this.id = id;
        }

        public void set(Thickness thickness, FontStyle style, BitmapFont font) {
            this.fontTable.put(thickness, style, font);
        }

        public FontInfo build() {
            return new FontInfo(id, (thickness, style) -> {
                Thickness finalThickness = Thickness.closestTo(thickness.amount, fontTable.rowKeySet());
                BitmapFont bitmapFont = this.fontTable.get(finalThickness, style);
                return bitmapFont;
            });
        }
    }

}
