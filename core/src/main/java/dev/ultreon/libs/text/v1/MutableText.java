package dev.ultreon.libs.text.v1;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.text.AttributedString;
import java.util.List;
import java.util.*;

import static java.text.AttributedCharacterIterator.Attribute;

public abstract class MutableText extends TextObject implements Cloneable {
    final List<TextObject> extras = new ArrayList<>();
    private final Map<Attribute, Object> attrs = new HashMap<>();
    private Color color;
    private boolean underlined;
    private boolean italic;
    private boolean bold;
    private boolean strikethrough;
    private boolean obfuscated;
    private boolean ligaturesEnabled;
    private boolean kerningEnabled;
    private double size;
    private FontWidth fontWidth;
    private float weight;
    private @NotNull FontWeight fontWeight = FontWeight.REGULAR;
    private @Range(from = -7, to = 7) int superscript;

    protected MutableText() {

    }

    @Override
    public AttributedString getAttrString() {
        var builder = new AttributedStringBuilder();
        var string = this.createString();
        if (!string.isEmpty()) builder.append(new AttributedString(string, this.getAttrs()));
        for (var extra : this.extras) {
            builder.append(extra.getAttrString());
        }

        return builder.build();
    }

    @Override
    public final String getText() {
        var builder = new StringBuilder();
        builder.append(this.createString());
        for (var extra : this.extras) {
            builder.append(extra.getText());
        }
        return builder.toString();
    }

    public Color getColor() {
        return this.color;
    }

    public MutableText setColor(Color color) {
        this.color = color;
        return this;
    }

    public boolean isUnderlined() {
        return this.underlined;
    }

    public MutableText setUnderlined(boolean underlined) {
        this.underlined = underlined;
        return this;
    }

    public boolean isStrikethrough() {
        return this.strikethrough;
    }

    public MutableText setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    public boolean isLigaturesEnabled() {
        return this.ligaturesEnabled;
    }

    public MutableText setLigaturesEnabled(boolean ligaturesEnabled) {
        this.ligaturesEnabled = ligaturesEnabled;
        return this;
    }

    public double getSize() {
        return this.size;
    }

    public MutableText setSize(double size) {
        this.size = size;
        return this;
    }

    @Deprecated
    public float getWidth() {
        return 1;
    }

    @Deprecated
    public MutableText setWidth(float size) {
        return this;
    }

    public FontWidth getFontWidth() {
        return this.fontWidth;
    }

    public MutableText setFontWidth(FontWidth width) {
        this.fontWidth = width;
        return this;
    }

    public float getWeight() {
        return this.weight;
    }

    public MutableText setWeight(float weight) {
        this.weight = weight;
        return this;
    }

    public @NotNull FontWeight getFontWeight() {
        return this.fontWeight;
    }

    public MutableText setFontWeight(@NotNull FontWeight weight) {
        this.fontWeight = weight;
        return this;
    }

    @Range(from = -7, to = 7)
    public int getSuperscript() {
        return this.superscript;
    }

    public MutableText setFontWeight(@Range(from = -7, to = 7) int superscript) {
        this.superscript = superscript;
        return this;
    }

    public Map<? extends Attribute, ?> getAttrs() {
        return this.attrs;
    }

    public MutableText append(TextObject textObject) {
        Preconditions.checkNotNull(textObject, "Appending text object is null.");

        try {
            var clone = this.clone();
            clone.extras.add(textObject);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public MutableText append(String text) {
        return this.append(TextObject.nullToEmpty(text));
    }

    public MutableText append(Object o) {
        return this.append(TextObject.nullToEmpty(String.valueOf(o)));
    }

    @Override
    protected MutableText clone() throws CloneNotSupportedException {
        return (MutableText) super.clone();
    }

    public boolean isItalic() {
        return this.italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isObfuscated() {
        return this.obfuscated;
    }

    public void setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public boolean isKerningEnabled() {
        return this.kerningEnabled;
    }

    public void setKerningEnabled(boolean kerningEnabled) {
        this.kerningEnabled = kerningEnabled;
    }
}
