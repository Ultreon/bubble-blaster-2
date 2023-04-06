package net.querz.nbt.tag;

import org.jetbrains.annotations.NotNull;
import org.checkerframework.common.value.qual.IntVal;

public class StringTag extends Tag<String> implements Comparable<StringTag> {

    public static final byte ID = 8;
    public static final String ZERO_VALUE = "";

    public StringTag() {
        super(ZERO_VALUE);
    }

    public StringTag(@NotNull String value) {
        super(value);
    }

    @Override
    @IntVal(ID)
    public byte getID() {
        return ID;
    }

    @NotNull
    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(@NotNull String value) {
        super.setValue(value);
    }

    @NotNull
    @Override
    public String valueToString(int maxDepth) {
        return escapeString(getValue(), false);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && getValue().equals(((StringTag) other).getValue());
    }

    @Override
    public int compareTo(StringTag o) {
        return getValue().compareTo(o.getValue());
    }

    @Override
    public StringTag clone() {
        return new StringTag(getValue());
    }
}
