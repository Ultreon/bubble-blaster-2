package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.graphics.Texture;
import com.google.common.collect.Lists;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.mod.ModDataManager;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.widget.Container;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ModListScreen extends Screen {
    private static final int GAP = 2;
    private static final int ENTRY_HEIGHT = 110;
    private final List<ModContainer> entries = Lists.newArrayList(FabricLoader.getInstance().getAllMods().stream().filter(modContainer -> modContainer.getContainingMod().isEmpty()).toList());
    private ObjectList<ModContainer> modList;
    private GuiComponent detailsPane;

    public ModListScreen() {
        super();

        this.entries.sort(Comparator.comparing(o -> o.getMetadata().getName()));
    }

    @Override
    public void init() {
        this.clearWidgets();

        var calcWidth = this.calculateWidth();
        this.modList = this.add(new ObjectList<>(this.entries, ENTRY_HEIGHT, GAP, 0, 0, calcWidth, this.height));
        this.modList.setSelectable(true);
        this.modList.setEntryRenderer(this::renderEntry);

        var entryAt = this.modList.getEntryAt(0, 0);
        if (entryAt != null) {
            this.modList.setSelected(entryAt);
        }

        this.detailsPane = this.add(new InfoContainer(calcWidth));
    }

    private int calculateWidth() {
        return Math.min(this.width - 50, 500);
    }

    @Override
    public boolean mouseWheel(int x, int y, float rotation) {
        return super.mouseWheel(x, y, rotation);
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.modList.setHeight(this.height);
        this.detailsPane.setX(this.modList.getWidth());
        this.detailsPane.setWidth(this.width - this.modList.getWidth());
        this.detailsPane.setHeight(this.height);

        super.render(renderer, mouseX, mouseY, deltaTime);
    }

    private void renderEntry(Renderer renderer, int width, int height, float y, ModContainer entry, boolean selected, boolean hovered) {
        var metadata = entry.getMetadata();

        renderer.fill(0, y, width, height, Color.argb(hovered ? 0x40ffffff : 0x20ffffff));
        if (selected)
            renderer.drawEffectBox(this.modList.getX() + 5, (int) (y + 5), width - 10, height - 10);

        renderer.scissored(this.modList.getX(), y, width, height, () -> {
            int iconSize = ENTRY_HEIGHT - 40;
            metadata.getIconPath(256).flatMap(entry::findPath).ifPresent(path1 -> {
                Texture tex = ModDataManager.getIcon(entry);
                renderer.blit(tex, this.modList.getX() + 20, y + 20, iconSize, iconSize);
            });

            int textX = this.modList.getX() + 20 + iconSize + 20;
            renderer.drawText(Fonts.MONOSPACED_BOLD_12.get(), metadata.getId(), textX, y + 20, Color.WHITE.withAlpha(0x80));
            renderer.drawText(Fonts.SANS_BOLD_32.get(), metadata.getName(), textX, y + 36, Color.WHITE);
            renderer.drawText(Fonts.SANS_ITALIC_16.get(), metadata.getDescription(), textX, y + height - 25, Color.WHITE.withAlpha(0x80));
        });
    }

    private class InfoContainer extends Container {
        public InfoContainer(int calcWidth) {
            super(calcWidth, 0, ModListScreen.this.width - calcWidth, ModListScreen.this.height);
        }

        @Override
        public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
            this.renderComponent(renderer);
            super.render(renderer, mouseX, mouseY, deltaTime);
        }

        @Override
        public void renderComponent(Renderer renderer) {
            var selected = ModListScreen.this.modList.getSelected();
            if (selected == null) return;
            var metadata = selected.value.getMetadata();

            this.layout.setText(Fonts.SANS_REGULAR_40.get(), metadata.getName() + "  ");

            int textX = this.x + 20;
            int textY = this.y + 20;

            renderer.drawText(Fonts.SANS_REGULAR_40.get(), metadata.getName(), textX, textY, Color.WHITE);
            renderer.drawText(Fonts.MONOSPACED_BOLD_24.get(), metadata.getVersion().getFriendlyString(), this.x + 20 + this.layout.width, textY + Fonts.SANS_REGULAR_48.get().getLineHeight(), Color.argb(0x80ffffff));
            renderer.drawText(Fonts.MONOSPACED_BOLD_12.get(), metadata.getId(), textX, textY + Fonts.SANS_REGULAR_48.get().getLineHeight() - Fonts.MONOSPACED_BOLD_12.get().getLineHeight(), Color.argb(0x80ffffff));
            String description = metadata.getDescription();
            AtomicInteger i = new AtomicInteger();
            description.lines().forEachOrdered(line -> renderer.drawText(Fonts.SANS_REGULAR_12.get(), line, textX, this.y + 90 + i.getAndIncrement() * (this.font.getLineHeight() + 1), Color.argb(0x60ffffff)));
        }
    }
}
