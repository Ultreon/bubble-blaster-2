package com.ultreon.bubbles.item;

import com.ultreon.bubbles.common.Registrable;

public abstract class ItemType extends Registrable implements IItemProvider {
    @Override
    public ItemType getItem() {
        return this;
    }

    @SuppressWarnings("EmptyMethod")
    public void onEntityTick() {

    }

    @SuppressWarnings("EmptyMethod")
    public void tick() {

    }
}
