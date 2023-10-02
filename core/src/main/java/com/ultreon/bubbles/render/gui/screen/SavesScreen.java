package com.ultreon.bubbles.render.gui.screen;

import com.google.common.collect.Lists;
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
import com.ultreon.bubbles.util.Either;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.libs.text.v1.TextObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class SavesScreen extends Screen {
    @Nullable
    private static SavesScreen instance;
    private final Map<GameSave, Either<GameSaveInfo, Exception>> cache = new HashMap<>();
    private final List<GameSave> saves;
    private final SaveLoader loader;
    @Nullable
    private ObjectList<GameSave> saveList;
    @Nullable private Button newSaveBtn;
    @Nullable private Button openSaveBtn;
    @Nullable private Button delSaveBtn;
    @Nullable private Button editSaveBtn;

    public SavesScreen(Screen backScreen) {
        super(backScreen);

        SavesScreen.instance = this;

        this.loader = SaveLoader.instance();
        this.loader.refresh();
        this.saves = Lists.newArrayList(this.loader.getSaves().stream().map(Supplier::get).toList());
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
                    BubbleBlaster.getLogger().error("Failed to delete save " + selected.value.getDirectory().getName() + ":", e);
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
        this.saves.addAll(this.loader.getSaves().stream().map(Supplier::get).toList());
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

    private void renderEntry(Renderer renderer, int width, int height, float y, GameSave save, boolean selected, boolean hovered) {
        var cachedInfo = this.cache.get(save);
        try {
            if (cachedInfo == null) {
                cachedInfo = Either.left(save.getInfo());
                this.cache.put(save, cachedInfo);
            }
        } catch (Exception e) {
            BubbleBlaster.getLogger().error(GameSave.MARKER, "Failed to load save information for " + save.getDirectory().getName() + ":", e);
            cachedInfo = Either.right(e);
            this.cache.put(save, cachedInfo);
        }

        if (cachedInfo.isRightPresent()) {
            var name = "Loading Error";
            var description = "Save filename: %s".formatted(save.getDirectory().getName());

            renderer.fill(0, 0, width, height, Color.RED.withAlpha(hovered ? 0x40 : 0x20));
            if (selected) {
                renderer.drawErrorEffectBox(10, 10, width - 20, height - 20, new Insets(2, 2, 2, 2));
            }

            renderer.drawText(Fonts.SANS_BOLD_20.get(), name, 20, 20, Color.WHITE.withAlpha(0xc0));
            renderer.drawText(Fonts.SANS_BOLD_14.get(), description, 20, 20 + Fonts.SANS_BOLD_20.get().getLineHeight() + 5, Color.WHITE.withAlpha(0x60));
            return;
        }

        var info = cachedInfo.getLeft();
        var name = info.getName();
        var description = info.getGamemode().getName().getText();
        description += ", " + LocalDateTime.ofInstant(Instant.ofEpochSecond(info.getSavedTime()), ZoneOffset.systemDefault()).format(DateTimeFormatter.ofPattern("dd/LLL/yyyy HH:mm:ss"));

        renderer.fill(0, 0, width, height, Color.WHITE.withAlpha(hovered ? 0x40 : 0x20));
        if (selected) {
            renderer.drawEffectBox(10, 10, width - 20, height - 20, new Insets(2, 2, 2, 2));
        }

        renderer.drawText(Fonts.SANS_BOLD_20.get(), name, 20, 20, Color.WHITE.withAlpha(0xc0));
        renderer.drawText(Fonts.SANS_BOLD_14.get(), description, 20, 20 + Fonts.SANS_BOLD_20.get().getLineHeight() + 5, Color.WHITE.withAlpha(0x60));
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
