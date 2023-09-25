package com.ultreon.bubbles.render.gui.screen;

import com.google.common.collect.Lists;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.widget.Container;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import com.ultreon.libs.commons.v0.Anchor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ModListScreen extends Screen {
    private static final int GAP = 2;
    private static final int ENTRY_HEIGHT = 110;
    private final List<ModContainer> entries = Lists.newArrayList(FabricLoader.getInstance().getAllMods());
    private ObjectList<ModContainer> modList;
    private GuiComponent detailsPane;

    public ModListScreen() {
        super();

        this.entries.sort(Comparator.comparing(o -> o.getMetadata().getName()));
    }

    @Override
    public void init() {
        this.clearWidgets();

        var calcWidth = calculateWidth();
        modList = add(new ObjectList<>(this.entries, ENTRY_HEIGHT, GAP, 0, 0, calcWidth, this.height));
        modList.setSelectable(true);
        modList.setEntryRenderer(this::renderEntry);

        var entryAt = modList.getEntryAt(0, 0);
        if (entryAt != null) {
            modList.setSelected(entryAt);
        }

        detailsPane = add(new Container(calcWidth, 0, width - calcWidth, height) {
            @Override
            public void render(Renderer renderer) {
                renderComponent(renderer);
                super.render(renderer);
            }

            @Override
            public void renderComponent(Renderer renderer) {
                var selected = modList.getSelected();
                if (selected == null) return;
                var metadata = selected.value.getMetadata();

                layout.setText(Fonts.SANS_REGULAR_40.get(), metadata.getName() + "  ");

                renderer.setColor(0xffffffff);
                renderer.drawText(Fonts.SANS_REGULAR_40.get(), metadata.getName(), 20, 20);
                renderer.setColor(0x80ffffff);
                renderer.drawText(Fonts.MONOSPACED_BOLD_24.get(), metadata.getVersion().getFriendlyString(), 20 + layout.width, 20 + Fonts.SANS_REGULAR_48.get().getLineHeight() / 2, Anchor.W);
                renderer.setColor(0x80ffffff);
                renderer.drawText(Fonts.MONOSPACED_BOLD_12.get(), metadata.getId(), 20, 70);
                String description = metadata.getDescription();
                AtomicInteger i = new AtomicInteger();
                renderer.setColor(0x60ffffff);
                description.lines().forEachOrdered(line -> renderer.drawText(Fonts.SANS_REGULAR_12.get(), line, 20, 90 + i.getAndIncrement() * (font.getLineHeight() + 1)));
            }
        });
    }

    private int calculateWidth() {
        return Math.min(width - 50, 500);
    }

    @Override
    public boolean mouseWheel(int x, int y, float rotation) {
        return super.mouseWheel(x, y, rotation);
    }

    @Override
    public void render(Renderer renderer) {
        modList.setHeight(this.height);
        detailsPane.setX(this.modList.getWidth());
        detailsPane.setWidth(this.width - modList.getWidth());
        detailsPane.setHeight(this.height);

        super.render(renderer);
    }

    private void renderEntry(Renderer renderer, int width, int height, ModContainer entry, boolean selected, boolean hovered) {
        var metadata = entry.getMetadata();

        fill(renderer, 0, 0, width, height, hovered ? 0x40ffffff : 0x20ffffff);

        int iconSize = ENTRY_HEIGHT - 40;
        metadata.getIconPath(256).flatMap(entry::findPath).ifPresent(path1 -> {
//            BufferedImage bufferedImage = ModDataManager.getIcon(entry);
//            renderer.image(bufferedImage, 20, 20, iconSize, iconSize);
        });

        if (selected) {
            renderer.drawEffectBox(10, 10, width - 20, height - 20, new Insets(2, 2, 2, 2));
        }

        int textX = 20 + iconSize + 20;

        renderer.setColor(0x7fffffff);
        renderer.drawText(Fonts.MONOSPACED_BOLD_12.get(), metadata.getId(), textX, 20);
        renderer.setColor(0xffffffff);
        renderer.drawText(Fonts.SANS_BOLD_32.get(), metadata.getName(), textX, 32);
        renderer.setColor(0x80ffffff);
        renderer.drawText(Fonts.SANS_REGULAR_14.get(), metadata.getDescription(), textX, ENTRY_HEIGHT - 18, Anchor.SW);
    }
}
