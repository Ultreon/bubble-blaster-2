package dev.ultreon.bubbles.shop;

import com.badlogic.gdx.Gdx;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.widget.ObjectList;
import dev.ultreon.bubbles.shop.entries.ShopEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ShopList extends ObjectList<ShopEntry> {
    private final List<ShopEntry> entries;

    public ShopList(List<@NotNull ShopEntry> entries, int x, int y, int width, int height) {
        super(entries, 80, 10, x, y, width, height);
        this.entries = entries;

        this.setBackgroundColor(0x00000000);
        this.setEntryRenderer(this::renderEntry);
    }

    public List<ShopEntry> getEntries() {
        return Collections.unmodifiableList(this.entries);
    }

    private void renderEntry(Renderer renderer, float width1, float height1, float y1, ShopEntry entry, boolean selected, boolean hovered1) {
        entry.setSize(500, (int) height1 - 10);
        entry.setPos(this.x + this.width / 2 - entry.getWidth() / 2, (int) (y1) + 10);
        entry.render(renderer, hovered1 ? entry.getX() : 0, hovered1 ? entry.getY() : 0, Gdx.graphics.getDeltaTime());
    }
}
