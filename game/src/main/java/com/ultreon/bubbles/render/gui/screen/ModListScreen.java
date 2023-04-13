package com.ultreon.bubbles.render.gui.screen;

import com.google.common.collect.Lists;
import com.ultreon.bubbles.mod.ModDataManager;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.widget.Container;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
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
                renderer.color(0xffffffff);
                font.draw(renderer, metadata.getName(), 48, 20, 20);
                renderer.color(0x80ffffff);
                monospaced.get().draw(renderer, metadata.getVersion().getFriendlyString(), 24, 20 + font.width(48, metadata.getName()) + 20, 20 + (float)font.height(48) / 2, Thickness.BOLD, Anchor.W);
                renderer.color(0x80ffffff);
                monospaced.get().draw(renderer, metadata.getId(), 12, 20, 70, Thickness.BOLD);
                String description = metadata.getDescription();
                AtomicInteger i = new AtomicInteger();
                renderer.color(0x60ffffff);
                description.lines().forEachOrdered(line -> font.draw(renderer, line, 12, 20, 90 + i.getAndIncrement() + font.height(12) + 1));
            }
        });
    }

    private int calculateWidth() {
        return Math.min(width - 50, 500);
    }

    @Override
    public boolean mouseWheel(int x, int y, double rotation, int amount, int units) {
        return super.mouseWheel(x, y, rotation, amount, units);
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
            BufferedImage bufferedImage = ModDataManager.getIcon(entry);
            renderer.hint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            renderer.image(bufferedImage, 20, 20, iconSize, iconSize);
            renderer.hint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        });

        if (selected) {
            renderer.drawEffectBox(10, 10, width - 20, height - 20, new Insets(2, 2, 2, 2));
        }

        Collection<Person> authors = metadata.getAuthors();

        int textX = 20 + iconSize + 20;

        renderer.color(0x7fffffff);
        monospaced.get().draw(renderer, metadata.getId(), 12, textX, 20, Thickness.BOLD);
        renderer.color(0xffffffff);
        font.draw(renderer, metadata.getName(), 40, textX, 32);
        var nameWidth = font.width(40, metadata.getName());
        renderer.color(0x50ffffff);
//        if (authors.isEmpty()) {
//            font.draw(renderer, metadata.getVersion().getFriendlyString(), 16, textX + nameWidth + 30, 52, Anchor.W);
//        } else {
//            font.draw(renderer, metadata.getVersion().getFriendlyString(), 16, textX + nameWidth + 30, 54, Anchor.SW);
//            font.draw(renderer, String.join(", ", authors.stream().map(Person::getName).toList()), 16, textX + nameWidth + 30, 50, Anchor.NW);
//        }
        renderer.color(0x80ffffff);
        font.draw(renderer, metadata.getDescription(), 14, textX, ENTRY_HEIGHT - 18, Anchor.SW);
    }
}
