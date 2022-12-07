package com.ultreon.bubbles.item.collection;

import com.ultreon.bubbles.item.Item;

public interface BaseItemCollection {
    int size();

    Item[] getItems();

    void setItem(int index, Item item);

    void clear();

    Item getItem(int slot);

    void removeItem(int index);

    void tick();
}
