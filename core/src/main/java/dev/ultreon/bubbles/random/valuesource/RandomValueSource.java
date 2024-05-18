package dev.ultreon.bubbles.random.valuesource;

import dev.ultreon.bubbles.random.JavaRandom;
import dev.ultreon.bubbles.random.RandomSource;
import dev.ultreon.bubbles.util.helpers.MathHelper;
import dev.ultreon.libs.text.v1.MutableText;
import dev.ultreon.libs.text.v1.TextObject;

import java.util.Objects;

public class RandomValueSource implements ValueSource {
    private final RandomSource random;
    private final double min;
    private final double max;

    private RandomValueSource(double min, double max) {
        this.random = new JavaRandom();
        this.min = min;
        this.max = max;
    }

    private RandomValueSource(long seed, double min, double max) {
        this.random = new JavaRandom(seed);
        this.min = min;
        this.max = max;
    }

    @Override
    public double getValue() {
        return this.random.nextDouble(this.min, this.max);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public static RandomValueSource random(int min, int max) {
        return new RandomValueSource(min, max);
    }

    public static RandomValueSource random(float min, float max) {
        return new RandomValueSource(min, max);
    }

    public static RandomValueSource random(double min, double max) {
        return new RandomValueSource(min, max);
    }

    public static RandomValueSource seeded(long seed, int min, int max) {
        return new RandomValueSource(seed, min, max);
    }

    public static RandomValueSource seeded(long seed, float min, float max) {
        return new RandomValueSource(seed, min, max);
    }

    public static RandomValueSource seeded(long seed, double min, double max) {
        return new RandomValueSource(seed, min, max);
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
        var that = (RandomValueSource) o;
        return Double.compare(this.min, that.min) == 0 && Double.compare(this.max, that.max) == 0 && Objects.equals(this.random, that.random);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.random, this.min, this.max);
    }
}
