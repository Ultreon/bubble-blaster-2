package com.ultreon.bubbles.platform.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.google.common.collect.Lists;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.render.gui.widget.Container;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ModListScreen extends Screen {
    private static final int GAP = 2;
    private static final int ENTRY_HEIGHT = 110;
    private final List<ModContainer> entries = Lists.newArrayList(FabricLoader.getInstance().getAllMods().stream().filter(modContainer -> modContainer.getContainingMod().isEmpty()).collect(Collectors.toList()));
    private ObjectList<ModContainer> modList;
    private GuiComponent detailsPane;

    public ModListScreen() {
        super();

        this.entries.sort(Comparator.comparing(o -> o.getMetadata().getName()));
    }

    @Override
    public void init() {
        this.clearWidgets();

        int calcWidth = this.calculateWidth();
        this.modList = this.add(new ObjectList<>(this.entries, ENTRY_HEIGHT, GAP, 0, 0, calcWidth, this.height));
        this.modList.setSelectable(true);
        this.modList.setEntryRenderer(this::renderEntry);

        ObjectList.ListEntry<ModContainer, ? extends ModContainer> entryAt = this.modList.getEntryAt(0, 0);
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
        ModMetadata metadata = entry.getMetadata();

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
            ObjectList.ListEntry<ModContainer, ? extends ModContainer> selected = ModListScreen.this.modList.getSelected();
            if (selected == null) return;
            ModMetadata metadata = selected.value.getMetadata();

            AtomicInteger textX = new AtomicInteger(this.x + 20);
            int textY = this.y + 20;

            this.drawIcon(renderer, metadata, selected, textX, textY);
            this.drawModDetails(renderer, metadata, textX, textY);
        }

        private void drawIcon(Renderer renderer, ModMetadata metadata, ObjectList.ListEntry<ModContainer, ? extends ModContainer> selected, AtomicInteger textX, int textY) {
            metadata.getIconPath(256).flatMap(selected.value::findPath).ifPresent(path1 -> {
                try {
                    Texture tex = ModDataManager.getIcon(selected.value);
                    renderer.blit(tex, textX.get(), textY, 64, 64);
                    textX.addAndGet(80);
                } catch (RuntimeException e) {
                    BubbleBlaster.LOGGER.warn("Can't load and draw mod icon for '" + selected.value.getMetadata().getId() + "': " + e);
                }
            });
        }

        private void drawModDetails(Renderer renderer, ModMetadata metadata, AtomicInteger textX, int textY) {
            this.layout.setText(Fonts.SANS_REGULAR_40.get(), metadata.getName() + "  ");

            renderer.drawText(Fonts.SANS_REGULAR_40.get(), metadata.getName(), textX.get(), textY, Color.WHITE);
            renderer.drawText(Fonts.MONOSPACED_BOLD_24.get(), metadata.getVersion().getFriendlyString(), textX.get() + this.layout.width, textY + Fonts.SANS_REGULAR_40.get().getLineHeight() / 2 - Fonts.MONOSPACED_BOLD_12.get().getLineHeight() + 1, Color.argb(0x80ffffff));
            renderer.drawText(Fonts.MONOSPACED_BOLD_12.get(), metadata.getId(), textX.get(), textY + Fonts.SANS_REGULAR_48.get().getLineHeight() - Fonts.MONOSPACED_BOLD_12.get().getLineHeight(), Color.argb(0x80ffffff));
            renderer.drawText(Fonts.MONOSPACED_BOLD_12.get(), metadata.getId(), textX.get(), textY + Fonts.SANS_REGULAR_48.get().getLineHeight() - Fonts.MONOSPACED_BOLD_12.get().getLineHeight(), Color.argb(0x80ffffff));
            String description = metadata.getDescription();
            AtomicInteger i = new AtomicInteger();
            description.lines().forEachOrdered(line -> renderer.drawText(Fonts.SANS_REGULAR_12.get(), line, textX.get(), this.y + 90 + i.getAndUpdate(this::addFontHeight) * (this.font.getLineHeight() + 1), Color.argb(0x60ffffff)));
        }

        private int addFontHeight(int i1) {
            return i1 + MathUtils.ceilPositive(this.font.getLineHeight() + 1);
        }
    }
}
