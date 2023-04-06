package net.querz.nbt.tag;

import net.querz.io.MaxDepthIO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.checkerframework.common.value.qual.IntVal;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.function.BiConsumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CompoundTag extends Tag<Map<String, Tag<?>>>
        implements Iterable<Map.Entry<String, Tag<?>>>, Comparable<CompoundTag>, MaxDepthIO {

    public static final byte ID = 10;

    public CompoundTag() {
        super(createEmptyValue());
    }

    public CompoundTag(int initialCapacity) {
        super(new HashMap<>(initialCapacity));
    }

    @Override
    @IntVal(ID)
    public byte getID() {
        return ID;
    }

    private static Map<String, Tag<?>> createEmptyValue() {
        return new HashMap<>(8);
    }

    public int size() {
        return getValue().size();
    }

    @Nullable
    public Tag<?> remove(String key) {
        return getValue().remove(key);
    }

    public void clear() {
        getValue().clear();
    }

    public boolean containsKey(String key) {
        return getValue().containsKey(key);
    }

    public boolean containsValue(Tag<?> value) {
        return getValue().containsValue(value);
    }

    @NotNull
    public Collection<Tag<?>> values() {
        return getValue().values();
    }

    @NotNull
    public Set<String> keySet() {
        return getValue().keySet();
    }

    @NotNull
    public Set<Map.Entry<String, Tag<?>>> entrySet() {
        return new NotNullEntrySet<>(getValue().entrySet());
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, Tag<?>>> iterator() {
        return entrySet().iterator();
    }

    public void forEach(@NotNull BiConsumer<String, Tag<?>> action) {
        getValue().forEach(action);
    }

    @Nullable
    public <C extends Tag<?>> C get(String key, Class<C> type) {
        Tag<?> t = getValue().get(key);
        if (t != null) {
            return type.cast(t);
        }
        return null;
    }

    public Tag<?> get(String key) {
        return getValue().get(key);
    }

    public NumberTag<?> getNumberTag(String key) {
        return (NumberTag<?>) getValue().get(key);
    }

    public Number getNumber(String key) {
        return getNumberTag(key).getValue();
    }

    public ByteTag getByteTag(String key) {
        return get(key, ByteTag.class);
    }

    public ShortTag getShortTag(String key) {
        return get(key, ShortTag.class);
    }

    public IntTag getIntTag(String key) {
        return get(key, IntTag.class);
    }

    public LongTag getLongTag(String key) {
        return get(key, LongTag.class);
    }

    public FloatTag getFloatTag(String key) {
        return get(key, FloatTag.class);
    }

    public DoubleTag getDoubleTag(String key) {
        return get(key, DoubleTag.class);
    }

    public StringTag getStringTag(String key) {
        return get(key, StringTag.class);
    }

    public ByteArrayTag getByteArrayTag(String key) {
        return get(key, ByteArrayTag.class);
    }

    public IntArrayTag getIntArrayTag(String key) {
        return get(key, IntArrayTag.class);
    }

    public LongArrayTag getLongArrayTag(String key) {
        return get(key, LongArrayTag.class);
    }

    public ListTag<?> getListTag(String key) {
        return get(key, ListTag.class);
    }

    public CompoundTag getCompoundTag(String key) {
        return get(key, CompoundTag.class);
    }

    public boolean getBoolean(String key) {
        Tag<?> t = get(key);
        return t instanceof ByteTag b && b.asByte() > 0;
    }

    public boolean getBoolean(String key, boolean def) {
        Tag<?> t = get(key);
        if (!(t instanceof ByteTag byteTag)) {
            return def;
        }
        return byteTag.asByte() > 0;
    }

    public byte getByte(String key) {
        ByteTag t = getByteTag(key);
        return t == null ? ByteTag.ZERO_VALUE : t.asByte();
    }

    public byte getByte(String key, byte def) {
        ByteTag t = getByteTag(key);
        return t == null ? def : t.asByte();
    }

    public short getShort(String key) {
        ShortTag t = getShortTag(key);
        return t == null ? ShortTag.ZERO_VALUE : t.asShort();
    }

    public short getShort(String key, short def) {
        ShortTag t = getShortTag(key);
        return t == null ? def : t.asShort();
    }

    public int getInt(String key) {
        IntTag t = getIntTag(key);
        return t == null ? IntTag.ZERO_VALUE : t.asInt();
    }

    public int getInt(String key, int def) {
        IntTag t = getIntTag(key);
        return t == null ? def : t.asInt();
    }

    public long getLong(String key) {
        LongTag t = getLongTag(key);
        return t == null ? LongTag.ZERO_VALUE : t.asLong();
    }

    public long getLong(String key, long def) {
        LongTag t = getLongTag(key);
        return t == null ? def : t.asLong();
    }

    public float getFloat(String key) {
        FloatTag t = getFloatTag(key);
        return t == null ? FloatTag.ZERO_VALUE : t.asFloat();
    }

    public float getFloat(String key, float def) {
        FloatTag t = getFloatTag(key);
        return t == null ? def : t.asFloat();
    }

    public double getDouble(String key) {
        DoubleTag t = getDoubleTag(key);
        return t == null ? DoubleTag.ZERO_VALUE : t.asDouble();
    }

    public double getDouble(String key, double def) {
        DoubleTag t = getDoubleTag(key);
        return t == null ? def : t.asDouble();
    }

    @NotNull
    public String getString(String key) {
        StringTag t = getStringTag(key);
        return t == null ? StringTag.ZERO_VALUE : t.getValue();
    }

    @Nullable
    public String getString(String key, @Nullable String def) {
        StringTag t = getStringTag(key);
        return t == null ? def : t.getValue();
    }

    public byte @NotNull [] getByteArray(String key) {
        ByteArrayTag t = getByteArrayTag(key);
        return t == null ? ByteArrayTag.ZERO_VALUE : t.getValue();
    }

    public byte @NotNull [] getByteArray(String key, byte @NotNull [] def) {
        ByteArrayTag t = getByteArrayTag(key);
        return t == null ? def : t.getValue();
    }

    public int @NotNull [] getIntArray(String key) {
        IntArrayTag t = getIntArrayTag(key);
        return t == null ? IntArrayTag.ZERO_VALUE : t.getValue();
    }

    public int @NotNull [] getIntArray(String key, int @NotNull [] def) {
        IntArrayTag t = getIntArrayTag(key);
        return t == null ? def : t.getValue();
    }

    public long @NotNull [] getLongArray(String key) {
        LongArrayTag t = getLongArrayTag(key);
        return t == null ? LongArrayTag.ZERO_VALUE : t.getValue();
    }

    public long @NotNull [] getLongArray(String key, long @NotNull [] def) {
        LongArrayTag t = getLongArrayTag(key);
        return t == null ? def : t.getValue();
    }

    public Tag<?> put(@NotNull String key, @NotNull Tag<?> tag) {
        return getValue().put(Objects.requireNonNull(key), Objects.requireNonNull(tag));
    }

    public Tag<?> putIfNotNull(String key, Tag<?> tag) {
        if (tag == null) {
            return this;
        }
        return put(key, tag);
    }

    @NotNull
    public Tag<?> putBoolean(String key, boolean value) {
        return put(key, new ByteTag(value));
    }

    @NotNull
    public Tag<?> putByte(String key, byte value) {
        return put(key, new ByteTag(value));
    }

    @NotNull
    public Tag<?> putShort(String key, short value) {
        return put(key, new ShortTag(value));
    }

    @NotNull
    public Tag<?> putInt(String key, int value) {
        return put(key, new IntTag(value));
    }

    @NotNull
    public Tag<?> putLong(String key, long value) {
        return put(key, new LongTag(value));
    }

    @NotNull
    public Tag<?> putFloat(String key, float value) {
        return put(key, new FloatTag(value));
    }

    @NotNull
    public Tag<?> putDouble(String key, double value) {
        return put(key, new DoubleTag(value));
    }

    @NotNull
    public Tag<?> putString(String key, String value) {
        return put(key, new StringTag(value));
    }

    @NotNull
    public Tag<?> putByteArray(String key, byte[] value) {
        return put(key, new ByteArrayTag(value));
    }

    @NotNull
    public Tag<?> putIntArray(String key, int[] value) {
        return put(key, new IntArrayTag(value));
    }

    @NotNull
    public Tag<?> putLongArray(String key, long[] value) {
        return put(key, new LongArrayTag(value));
    }

    @Override
    @NotNull
    public String valueToString(int maxDepth) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
            sb.append(first ? "" : ",")
                    .append(escapeString(e.getKey(), false)).append(":")
                    .append(e.getValue().toString(decrementMaxDepth(maxDepth)));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other) || size() != ((CompoundTag) other).size()) {
            return false;
        }
        for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
            Tag<?> v;
            if ((v = ((CompoundTag) other).get(e.getKey())) == null || !e.getValue().equals(v)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(CompoundTag o) {
        return Integer.compare(size(), o.getValue().size());
    }

    @NotNull
    @Override
    public CompoundTag clone() {
        // Choose initial capacity based on default load factor (0.75) so all entries fit in map without resizing
        CompoundTag copy = new CompoundTag((int) Math.ceil(getValue().size() / 0.75f));
        for (Map.Entry<String, Tag<?>> e : getValue().entrySet()) {
            copy.put(e.getKey(), e.getValue().clone());
        }
        return copy;
    }
}
