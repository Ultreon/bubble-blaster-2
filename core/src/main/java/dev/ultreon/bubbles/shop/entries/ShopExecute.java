package dev.ultreon.bubbles.shop.entries;

import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.shop.CoinContainer;
import dev.ultreon.bubbles.util.Callback;

public class ShopExecute extends ShopEntry {
    private final Callback<Player> onPurchase;
    private final String displayName;

    public ShopExecute(String displayName, CoinContainer price, Callback<Player> onPurchase) {
        super(price);
        this.onPurchase = onPurchase;
        this.displayName = displayName;
    }

    @Override
    public void purchase(Player buyer) {
        this.onPurchase.call(buyer);
    }

    @Override
    protected String getDisplayName() {
        return this.displayName;
    }
}
