package dev.ultreon.bubbles.item;

public abstract class ItemType implements IItemProvider {
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
