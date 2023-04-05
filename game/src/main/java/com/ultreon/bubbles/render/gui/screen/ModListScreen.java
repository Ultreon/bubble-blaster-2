package com.ultreon.bubbles.render.gui.screen;

import com.google.common.collect.Lists;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.mod.ModDataManager;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.bubbles.render.gui.widget.Rectangle;
import com.ultreon.bubbles.render.gui.widget.ScrollPane;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.vector.Vec2i;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ModListScreen extends Screen {
    private static final int GAP = 1;
    private static final int ENTRY_HEIGHT = 150;
    private static final Font TITLE_FONT = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD, 36);
    private static final Font DESCRIPTION_FONT = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.PLAIN, 14);
    private static final Font DETAILS_FONT = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD, 14);
    private static final Font DETAILS_FONT_LARGE = new Font(BubbleBlaster.getInstance().getSansFontName(), Font.BOLD, 20);
    private final List<ModContainer> entries = Lists.newArrayList(FabricLoader.getInstance().getAllMods());

    public ModListScreen() {
        super();

        entries.sort(Comparator.comparing(o -> o.getMetadata().getName()));
    }

    @Override
    public void init() {
        this.clearWidgets();

        var calcWidth = calculateWidth();
        var calcHeight = calculateHeight();
        var entryPane = new ScrollPane(
                new Rectangle(0, 0, calcWidth - ScrollPane.SCROLLBAR_WIDTH, calcHeight),
                (width - calcWidth) / 2, 50, calcWidth, height - 100
        );
        entryPane.add(new GuiComponent(0, 0, width, calcHeight) {
            @Override
            public void render(Renderer renderer) {
                var y = 0;
                for (var entry : entries) {
                    var metadata = entry.getMetadata();
                    GuiStateListener.fill(renderer, 0, y, ModListScreen.this.width, ENTRY_HEIGHT, 0xff444444);
                    int finalY = y;
                    metadata.getIconPath(256).flatMap(entry::findPath).ifPresent(path1 -> {
                        BufferedImage bufferedImage = ModDataManager.getIcon(entry);
                        renderer.hint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        renderer.image(bufferedImage, 20, finalY + 20, 110, 110);
                        renderer.hint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    });

                    Collection<Person> authors = metadata.getAuthors();

                    renderer.color(0x7fffffff);
                    GraphicsUtils.drawLeftAnchoredString(renderer, metadata.getId(), new Vec2i(150, y + 5), 15, new Font(Font.MONOSPACED, Font.PLAIN, 12));
                    renderer.color(0xffffffff);
                    GraphicsUtils.drawLeftAnchoredString(renderer, metadata.getName(), new Vec2i(150, y + 20), 40, TITLE_FONT);
                    var nameWidth = renderer.fontMetrics(TITLE_FONT).stringWidth(metadata.getName());
                    renderer.color(0x50ffffff);
                    if (authors.isEmpty()) {
                        GraphicsUtils.drawLeftAnchoredString(renderer, metadata.getVersion().getFriendlyString(), new Vec2i(150 + nameWidth + 30, y + 20), 40, DETAILS_FONT_LARGE);
                    } else {
                        GraphicsUtils.drawLeftAnchoredString(renderer, metadata.getVersion().getFriendlyString(), new Vec2i(150 + nameWidth + 30, y + 20), 20, DETAILS_FONT);
                        GraphicsUtils.drawLeftAnchoredString(renderer, String.join(", ", authors.stream().map(Person::getName).toList()), new Vec2i(150 + nameWidth + 30, y + 40), 20, DETAILS_FONT);
                    }
                    renderer.color(0x80ffffff);
                    GraphicsUtils.drawLeftAnchoredStringML(renderer, metadata.getDescription(), new Vec2i(150, y + 65), 15, DESCRIPTION_FONT);
                    y += ENTRY_HEIGHT + GAP;
                }
            }

            @Override
            public void tick() {

            }
        });

        add(entryPane);
    }

    private int calculateHeight() {
        return entries.size() * (ENTRY_HEIGHT + GAP) - GAP;
    }

    private int calculateWidth() {
        return Math.min(width - 50, 750);
    }

    @Override
    public void mouseWheel(int x, int y, double rotation, int amount, int units) {
        super.mouseWheel(x, y, rotation, amount, units);
    }
}
