package com.ultreon.bubbles.common.holders;

import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;

public interface ListDataHolder<T extends Tag<?>> {
    ListTag<?> save();

    void load(ListTag<T> array);
}
