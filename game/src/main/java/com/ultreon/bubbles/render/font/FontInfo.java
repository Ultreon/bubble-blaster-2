package com.ultreon.bubbles.render.font;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.awt.Font;
import java.util.function.BiFunction;

public class FontInfo {
    private final BiFunction<Thickness, FontStyle, Font> fontGetter;

    private FontInfo(BiFunction<Thickness, FontStyle, Font> fontGetter) {
        this.fontGetter = fontGetter;
    }

    public Font getFont(Thickness thickness, FontStyle style) {
        return this.fontGetter.apply(thickness, style);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Table<Thickness, FontStyle, Font> fontTable = HashBasedTable.create();

        public Builder() {

        }

        public void set(Thickness thickness, FontStyle style, Font font) {
            this.fontTable.put(thickness, style, font);
        }

        public FontInfo build() {
            return new FontInfo((thickness, style) -> {
                Thickness finalThickness = Thickness.closestTo(thickness.amount, fontTable.rowKeySet());
                return this.fontTable.get(finalThickness, style);
            });
        }
    }

}
