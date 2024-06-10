package dev.ultreon.bubbles.shop;

public class CoinContainer {
    private final int goldCoins;
    private final int silverCoins;

    public CoinContainer(int goldCoins, int silverCoins) {
        this.goldCoins = goldCoins;
        this.silverCoins = silverCoins;
    }

    public int getGoldCoins() {
        return this.goldCoins;
    }

    public int getSilverCoins() {
        return this.silverCoins;
    }
}
