package com.ultreon.bubbles.random.valuesource;

import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.libs.text.v1.MutableText;
import com.ultreon.libs.text.v1.TextObject;

import java.util.Objects;

public class ConstantValueSource implements ValueSource {
    private final double value;

    private ConstantValueSource(double value) {
        this.value = value;
    }

    private ConstantValueSource() {
        this.value = 0f;
    }

    public static ValueSource of(int value) {
        return new ConstantValueSource(value);
    }

    public static ValueSource of(float value) {
        return new ConstantValueSource(value);
    }

    public static ValueSource of(double value) {
        return new ConstantValueSource(value);
    }

    public static ValueSource of() {
        return new ConstantValueSource(0);
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ConstantValueSource that = (ConstantValueSource) o;
        return Double.compare(this.value, that.value) == 0;
    }

    @Override
    public String getTranslationPath() {
        return ValueSource.super.getTranslationPath();
    }

    @Override
    public MutableText getTranslation() {
        return TextObject.literal(String.valueOf(MathHelper.toReadableString(this.value)));
    }

    @Override
    public String getTranslationText() {
        return String.valueOf(MathHelper.round(this.value, 5));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }
}
