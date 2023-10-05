package com.ultreon.bubbles.random.valuesource;

import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.libs.text.v1.MutableText;
import com.ultreon.libs.text.v1.TextObject;

import java.util.Objects;
import java.util.Random;

public class RngValueSource implements ValueSource {
    private final Rng rng;
    private final long seed;
    private final int min;
    private final int max;

    public RngValueSource(Rng rng,  int min, int max) {
        this(rng, new Random(System.currentTimeMillis()).nextLong(), min, max);
    }

    public RngValueSource(Rng rng, long seed, int min, int max) {
        this.rng = rng;
        this.seed = seed;
        this.min = min;
        this.max = max;
    }

    @Override
    public double getValue() {
        return this.rng.getNumber(this.min, this.max, this.seed);
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public MutableText getTranslation() {
        return TextObject.translation("bubbleblaster.misc.valueSource.between", MathHelper.toReadableString(this.min), MathHelper.toReadableString(this.max));
    }

    @Override
    public String getTranslationText() {
        return this.getTranslation().getText();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        RngValueSource that = (RngValueSource) o;
        return this.seed == that.seed && this.min == that.min && this.max == that.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.seed, this.min, this.max);
    }
}
