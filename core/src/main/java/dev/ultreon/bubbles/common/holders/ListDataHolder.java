package dev.ultreon.bubbles.common.holders;

import dev.ultreon.ubo.types.DataType;
import dev.ultreon.ubo.types.ListType;

public interface ListDataHolder<T extends DataType<?>> {
    ListType<T> save();

    void load(ListType<T> array);
}
