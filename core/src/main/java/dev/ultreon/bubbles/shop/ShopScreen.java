package dev.ultreon.bubbles.shop;

import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.bubbles.shop.entries.ShopEntry;
import dev.ultreon.libs.text.v1.TextObject;

import java.util.List;

public class ShopScreen extends Screen {
    private final List<ShopEntry> entries;

    public ShopScreen() {
        super(TextObject.translation("bubbleblaster.screen.shop.title"));
        this.entries = this.buildEntries();
    }

    private List<ShopEntry> buildEntries() {
        return List.copyOf(Registries.SHOP_ENTRIES.values());
    }

    public void purchase(ShopEntry entry, Player buyer) {
        entry.purchase(buyer);
    }

    @Override
    public void init() {
        this.add(new ShopList(this.entries, 0, 0, this.width, this.height));
    }
}
