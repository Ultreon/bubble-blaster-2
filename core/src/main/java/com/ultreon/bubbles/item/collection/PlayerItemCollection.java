package com.ultreon.bubbles.item.collection;

import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerItemCollection extends ItemCollection {
    private final Player player;
    private final List<Player> watchers = new ArrayList<>();
    private Item[] items = new Item[this.size()];

    public PlayerItemCollection(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @SuppressWarnings("EmptyMethod")
    public void openInventoryTo() {

    }

    public List<Player> getWatchers() {
        return Collections.unmodifiableList(this.watchers);
    }

    @Override
    public int size() {
        return 18;
    }

    @Override
    public Item[] getItems() {
        return this.items;
    }

    @Override
    public void setItem(int slot, Item item) {
        this.items[slot] = item;
    }

    @Override
    public void clear() {
        this.items = new Item[this.size()];
    }

    @Override
    public Item getItem(int slot) {
        return this.items[slot];
    }

    @Override
    public void removeItem(int slot) {
        this.items[slot] = null;
    }

    @Override
    public void tick() {
        for (var item : this.items) {
            if (item != null) {
                item.onEntityTick(this.player);
                item.tick();
            }
        }
    }
}
