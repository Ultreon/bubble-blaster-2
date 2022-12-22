package com.ultreon.bubbles.common.holders;

import com.ultreon.data.types.IType;
import com.ultreon.data.types.ListType;

public interface ListDataHolder<T extends IType<?>> {
    ListType<T> save();

    void load(ListType<T> array);
}
