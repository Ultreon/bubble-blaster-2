package com.ultreon.bubbles.render.gui.screen;

import com.google.common.collect.Lists;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.bubbles.render.gui.widget.Label;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import com.ultreon.bubbles.render.gui.widget.TextEntry;
import com.ultreon.libs.text.v1.TextObject;

import java.util.Collections;
import java.util.List;

public class CreateSaveScreen extends Screen {
    private final List<Gamemode> gamemodes;
    private TextEntry seedEntry;
    private ObjectList<Gamemode> gamemodeList;
    private Button createBtn;

    public CreateSaveScreen(Screen back) {
        this.gamemodes = Collections.unmodifiableList(Lists.newArrayList(Registries.GAMEMODES.values()));
        this.setBackScreen(back);
    }

    @Override
    public void init() {
        Label label = this.add(new Label(TextObject.translation("bubbleblaster.saves.create.title"), this.width / 2 - 150, 0, 300, 50));
        label.setFontSize(36);

        Label label1 = this.add(new Label(TextObject.translation("bubbleblaster.saves.create.seed"), this.width / 2 - 150, 50, 300, 30));
        label1.setFontSize(20);

        Label label2 = this.add(new Label(TextObject.translation("bubbleblaster.saves.create.gamemode"), this.width / 2 - 150, 95, 300, 25));
        label2.setFontSize(20);

        this.seedEntry = this.add(new TextEntry.Builder()
                .bounds(this.width / 2 - 150, 60, 300, 30)
                .build());
        this.gamemodeList = this.add(new ObjectList<>(this.gamemodes, 30, 2, this.width / 2 - 150, 60 + 60, 300, 300));
        this.gamemodeList.setSelectable(true);
        this.gamemodeList.setEntryRenderer(this::renderEntry);
        this.createBtn = this.add(Button.builder()
                .bounds(this.width / 2 - 100, 60 + 370, 200, 40)
                .text(TextObject.translation("bubbleblaster.screen.saves.create.button"))
                .command(this::create)
                .build());
    }

    private void create() {
        ObjectList.ListEntry<Gamemode, ? extends Gamemode> selected = this.gamemodeList.getSelected();
        if (selected == null) {
            return;
        }

        long seed = this.game.serializeSeed(this.seedEntry.getText());
        this.game.createGame(seed, selected.value);
    }

    private void renderEntry(Renderer renderer, float width, float height, float y, Gamemode gamemode, boolean selected, boolean hovered) {
        String name = gamemode.getName().getText();

        int x = this.gamemodeList.getX();

        renderer.fill(x, this.y, width, height, Color.WHITE.withAlpha(hovered ? 0x40 : 0x20));
        if (selected) {
            renderer.drawEffectBox(x + 2, this.y + 2, width - 4, height - 4, new Insets(2, 2, 2, 2));
        }

        renderer.drawText(this.font, name, x + 10, this.y + height / 2f, Color.WHITE.withAlpha(0xc0));
    }

    @Override
    public void renderBackground(Renderer renderer) {
        super.renderBackground(renderer);

//        font.draw(renderer, TextObject.translation("bubbleblaster.saves.create.title"), 36, width / 2f, height / 2f - 30, Thickness.BOLD, Anchor.S);
//        font.draw(renderer, TextObject.translation("bubbleblaster.saves.create.seed"), 36, width / 2f - 20, height / 2f + 15, Thickness.BOLD, Anchor.E);
//        font.draw(renderer, TextObject.translation("bubbleblaster.saves.create.gamemode"), 36, width / 2f - 20, height / 2f + 55, Thickness.BOLD, Anchor.E);
    }

    public TextEntry getSeedEntry() {
        return this.seedEntry;
    }

    public ObjectList<Gamemode> getGamemodeList() {
        return this.gamemodeList;
    }

    public Button getCreateBtn() {
        return this.createBtn;
    }
}
