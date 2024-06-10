package dev.ultreon.bubbles.shop.entries;

import com.badlogic.gdx.Input;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.init.Fonts;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Insets;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.GuiComponent;
import dev.ultreon.bubbles.shop.CoinContainer;

import static dev.ultreon.bubbles.BubbleBlaster.id;

public abstract class ShopEntry extends GuiComponent {
    private final CoinContainer price;

    public ShopEntry(CoinContainer price) {
        super(0, 0, 100, 80);
        this.price = price;
    }

    public CoinContainer getPrice() {
        return this.price;
    }

    public abstract void purchase(Player buyer);

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.drawText(Fonts.MONOSPACED_BOLD.get(), String.valueOf(this.price.getGoldCoins()), this.x + 30, this.y + 42, this.canPurchase(this.game.player) ? Color.WHITE.withAlpha(0xa0) : Color.WHITE.withAlpha(0x80));
        renderer.drawText(Fonts.MONOSPACED_BOLD.get(), String.valueOf(this.price.getSilverCoins()), this.x + 80, this.y + 42, this.canPurchase(this.game.player) ? Color.WHITE.withAlpha(0xa0) : Color.WHITE.withAlpha(0x80));

        renderer.blit(this.game.getTextureManager().getOrLoadTexture(id("ui/coin_gold")), this.x + 10, this.y + 40, 16, 16);
        renderer.blit(this.game.getTextureManager().getOrLoadTexture(id("ui/coin_silver")), this.x + 60, this.y + 40, 16, 16);

        renderer.drawText(Fonts.SANS_HEADER_2.get(), this.getDisplayName(), this.x + 10, this.y + 10, this.canPurchase(this.game.player) ? Color.WHITE : Color.CRIMSON);

        renderer.box(this.x, this.y, this.getWidth(), this.getHeight(), (this.canPurchase(this.game.player) ? Color.WHITE : Color.CRIMSON).withAlpha(0x80), new Insets(1));

        if (this.isHovered()) {
            renderer.fill(this.x, this.y, this.getWidth(), this.getHeight(), (this.canPurchase(this.game.player) ? Color.WHITE : Color.CRIMSON).withAlpha(0x20));
        }
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        if (this.canPurchase(this.game.player)
                && button == Input.Buttons.LEFT) {
            this.game.player.buy(this);
            this.purchase(this.game.player);
            return true;
        }

        return super.mouseRelease(x, y, button);
    }

    public boolean canPurchase(Player player) {
        return player.getGoldCoins() >= this.price.getGoldCoins() && player.getSilverCoins() >= this.price.getSilverCoins();
    }

    protected abstract String getDisplayName();
}
