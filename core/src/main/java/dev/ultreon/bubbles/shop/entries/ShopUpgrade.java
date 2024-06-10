package dev.ultreon.bubbles.shop.entries;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.init.Fonts;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.shop.CoinContainer;

public class ShopUpgrade extends ShopEntry {
    private final Attribute attribute;
    private final double amount;
    private final int maxLevel;

    public ShopUpgrade(CoinContainer price, Attribute attribute, double amount, int maxLevel) {
        super(price);
        this.attribute = attribute;
        this.amount = amount;
        this.maxLevel = maxLevel;
    }

    @Override
    public void purchase(Player buyer) {
        double value = buyer.getAttributes().get(this.attribute);
        buyer.getAttributes().setBase(this.attribute, value + this.amount);
        buyer.activateUpgrade(this);
    }

    @Override
    public boolean canPurchase(Player player) {
        if (player.getUpgradeCount(this) >= this.maxLevel) return false;
        return super.canPurchase(player);
    }

    @Override
    protected String getDisplayName() {
        return this.attribute.getDisplayName() + " +" + this.amount + " (lvl " + (BubbleBlaster.getInstance().player.getUpgradeCount(this) + 1) + ")";
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        super.render(renderer, mouseX, mouseY, deltaTime);

        Player player = BubbleBlaster.getInstance().player;
        int level = player.getUpgradeCount(this);
        String text = "lvl " + level + "/" + this.maxLevel;
        renderer.drawTextRight(Fonts.MONOSPACED_BOLD.get(), text, this.x + this.width - 10, this.y + 42, this.canPurchase(player) ? Color.WHITE.withAlpha(0xa0) : Color.WHITE.withAlpha(0x80));
    }
}
