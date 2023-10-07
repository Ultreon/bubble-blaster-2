package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.save.GameSaveInfo;
import com.ultreon.bubbles.save.SaveLoader;
import com.ultreon.bubbles.util.Result;
import com.ultreon.libs.text.v1.TextObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"FieldCanBeLocal"})
public class SavesScreen extends Screen {
    @Nullable
    private static SavesScreen instance;
    private final Map<GameSave, Result<GameSaveInfo>> cache = new HashMap<>();
    private final List<GameSave> saves;
    private final SaveLoader loader;
    private ObjectList<GameSave> saveList;
    private Button newSaveBtn;
    private Button openSaveBtn;
    private Button delSaveBtn;
    private Button editSaveBtn;

    public SavesScreen(Screen backScreen) {
        super(backScreen);

        SavesScreen.instance = this;

        this.loader = SaveLoader.instance();
        this.loader.refresh();
        this.saves = this.loader.getSaves().stream().map(Supplier::get).collect(Collectors.toList());
    }

    @SuppressWarnings("EmptyMethod")
    private void newSave() {
        this.game.showScreen(new CreateSaveScreen(this));
    }

    private void openSave() {
        var saveList = this.saveList;
        if (saveList != null) {
            var selected = saveList.getSelected();
            if (selected != null) {
                try {
                    BubbleBlaster.getInstance().loadGame(selected.value);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void deleteSave() {
        var saveList = this.saveList;
        if (saveList != null) {
            var selected = saveList.getSelected();
            if (selected != null) {
                try {
                    selected.value.delete();
                } catch (IOException e) {
                    BubbleBlaster.getLogger().error("Failed to delete save " + selected.value.getHandle().name() + ":", e);
                }
                this.refresh();
            }
        }
    }

    private void editSave() {
        this.game.notifications.unavailable("Edit Save");
    }

    private void refresh() {
        this.loader.refresh();
        this.saves.clear();
        this.saves.addAll(this.loader.getSaves().stream().map(Supplier::get).collect(Collectors.toList()));
        this.game.showScreen(this);
    }

    @Nullable
    public static SavesScreen instance() {
        return instance;
    }

    @Override
    public void init() {
        this.clearWidgets();
        var calcWidth = this.calculateWidth();

        this.saveList = this.add(new ObjectList<>(this.saves, 130, 2, (this.width - calcWidth) / 2, 10, calcWidth, this.height - 120));
        this.saveList.setSelectable(true);
        this.saveList.setEntryRenderer(this::renderEntry);

        this.newSaveBtn = this.add(Button.builder().bounds((this.width - calcWidth) / 2, this.height - 100, calcWidth / 2 - 5, 40).text(TextObject.translation("bubbleblaster.screen.saves.new")).build());
        this.newSaveBtn.setCommand(this::newSave);

        this.openSaveBtn = this.add(Button.builder().bounds(this.width / 2 + 5, this.height - 100, calcWidth / 2 - 5, 40).text(TextObject.translation("bubbleblaster.screen.saves.open")).build());
        this.openSaveBtn.setCommand(this::openSave);

        this.delSaveBtn = this.add(Button.builder().bounds((this.width - calcWidth) / 2, this.height - 50, calcWidth / 2 - 5, 40).text(TextObject.translation("bubbleblaster.screen.saves.delete")).build());
        this.delSaveBtn.setCommand(this::deleteSave);

        this.editSaveBtn = this.add(Button.builder().bounds(this.width / 2 + 5, this.height - 50, calcWidth / 2 - 5, 40).text(TextObject.translation("bubbleblaster.screen.saves.edit")).build());
        this.editSaveBtn.setCommand(this::editSave);
    }

    private void renderEntry(Renderer renderer, float width, float height, float y, GameSave save, boolean selected, boolean hovered) {
        var cachedInfo = this.cache.get(save);
        var x = this.saveList.getX();

        try {
            if (cachedInfo == null) {
                cachedInfo = Result.success(save.getInfo());
                this.cache.put(save, cachedInfo);
            }
        } catch (Exception e) {
            BubbleBlaster.getLogger().error(GameSave.MARKER, "Failed to load save information for " + save.getHandle().name() + ":", e);
            cachedInfo = Result.failure(e);
            this.cache.put(save, cachedInfo);
        }

        if (cachedInfo.isFailure()) {
            var name = "Loading Error";
            var description = String.format("Save filename: %s", save.getHandle().name());

            renderer.fill(x, y, width, height, Color.RED.withAlpha(hovered ? 0x40 : 0x20));
            if (selected)
                renderer.drawErrorEffectBox(x, (int) y, (int) width, (int) height, new Insets(1, 1, 4, 1));
            else if (hovered)
                renderer.drawErrorEffectBox(x, (int) y, (int) width, (int) height, new Insets(1, 1, 1, 1));

            renderer.drawText(Fonts.SANS_HEADER_2.get(), name, x + 20, y + 20, Color.WHITE.withAlpha(0xc0));
            renderer.drawText(Fonts.SANS_PARAGRAPH_BOLD.get(), description, x + 20, y + 20 + Fonts.SANS_HEADER_2.get().getLineHeight() + 5, Color.WHITE.withAlpha(0x60));
            return;
        }

        final var info = cachedInfo.getValue();
        final var name = info.getName();
        final var description = info.getGamemode().getName().getText() + ", " + info.getSavedTimeFormatted();

        renderer.fill(x, y, width, height, Color.WHITE.withAlpha(hovered ? 0x40 : 0x20));
        if (selected)
            renderer.drawEffectBox(x, (int) y, (int) width, (int) height, new Insets(1, 1, 4, 1));
        else if (hovered)
            renderer.drawEffectBox(x, (int) y, (int) width, (int) height, new Insets(1, 1, 1, 1));

        renderer.drawText(Fonts.SANS_HEADER_2.get(), name, x + 20, y + 20, Color.WHITE.withAlpha(0xc0));
        renderer.drawText(Fonts.SANS_PARAGRAPH_BOLD.get(), description, x + 20, y + 20 + Fonts.SANS_HEADER_2.get().getLineHeight() + 5, Color.WHITE.withAlpha(0x60));
    }

    private int calculateWidth() {
        return Math.min(this.width - 50, 500);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.renderBackground(renderer);
        this.renderChildren(renderer, mouseX, mouseY, deltaTime);
    }
}
