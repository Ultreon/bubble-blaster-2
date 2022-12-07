package net.querz.nbt.tag;

import org.checkerframework.common.value.qual.IntRange;

public abstract class NumberTag<T extends Number & Comparable<T>> extends Tag<T> {

    public NumberTag(T value) {
        super(value);
    }

    @IntRange(from = Byte.MIN_VALUE, to = Byte.MAX_VALUE)
    public byte asByte() {
        return getValue().byteValue();
    }

    @IntRange(from = Short.MIN_VALUE, to = Short.MAX_VALUE)
    public short asShort() {
        return getValue().shortValue();
    }

    public int asInt() {
        return getValue().intValue();
    }

    public long asLong() {
        return getValue().longValue();
    }

    public float asFloat() {
        return getValue().floatValue();
    }

    public double asDouble() {
        return getValue().doubleValue();
    }

    @Override
    public String valueToString(int maxDepth) {
        return getValue().toString();
    }
}
