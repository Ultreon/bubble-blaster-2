package com.ultreon.bubbles.render.gui.screen;

import com.google.common.collect.Lists;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Anchor;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Label;
import com.ultreon.bubbles.render.gui.widget.ObjectList;
import com.ultreon.bubbles.render.gui.widget.OptionsButton;
import com.ultreon.bubbles.render.gui.widget.OptionsTextEntry;
import com.ultreon.libs.text.v0.TextObject;

import java.util.Collections;
import java.util.List;

public class CreateSaveScreen extends Screen {
    private final List<Gamemode> gamemodes;
    private OptionsTextEntry seedEntry;
    private ObjectList<Gamemode> gamemodeList;
    private OptionsButton createBtn;

    public CreateSaveScreen(Screen back) {
        this.gamemodes = Collections.unmodifiableList(Lists.newArrayList(Registries.GAMEMODES.values()));
        this.setBackScreen(back);
    }

    @Override
    public void init() {
        Label label = add(new Label(TextObject.translation("bubbles/saves/create/title"), this.width / 2 - 150, 0, 300, 50));
        label.setFontSize(36);

        Label label1 = add(new Label(TextObject.translation("bubbles/saves/create/seed"), this.width / 2 - 150, 50, 300, 30));
        label1.setFontSize(20);

        Label label2 = add(new Label(TextObject.translation("bubbles/saves/create/gamemode"), this.width / 2 - 150, 95, 300, 25));
        label2.setFontSize(20);

        this.seedEntry = add(new OptionsTextEntry.Builder()
                .bounds(this.width / 2 - 150, 60, 300, 30)
                .build());
        this.gamemodeList = add(new ObjectList<>(this.gamemodes, 30, 2, this.width / 2 - 150, 60 + 60, 300, 300));
        this.gamemodeList.setSelectable(true);
        this.gamemodeList.setEntryRenderer((renderer, width1, height1, y, gamemode, selected, hovered) -> renderEntry(renderer, width1, height1, gamemode, selected, hovered));
        this.createBtn = add(new OptionsButton.Builder()
                .bounds(this.width / 2 - 100, 60 + 370, 200, 40)
                .text(TextObject.translation("bubbles/screen/saves/create/button"))
                .command(this::create)
                .build());
    }

    private void create() {
        var selected = this.gamemodeList.getSelected();
        if (selected == null) {
            return;
        }

        long seed = this.game.serializeSeed(this.seedEntry.getText());
        this.game.createGame(seed, selected.value);
    }

    private void renderEntry(Renderer renderer, int width, int height, Gamemode gamemode, boolean selected, boolean hovered) {
        String name = gamemode.getName().getText();

        fill(renderer, 0, 0, width, height, hovered ? 0x40ffffff : 0x20ffffff);
        if (selected) {
            renderer.drawEffectBox(2, 2, width - 4, height - 4, new Insets(2, 2, 2, 2));
        }

        renderer.setColor(0xc0ffffff);
        renderer.drawText(font, name, 10, height / 2f, Anchor.W);
    }

    @Override
    public boolean onClose(Screen to) {
        return super.onClose(to);
    }

    @Override
    public boolean keyPress(int keyCode) {
        return super.keyPress(keyCode);
    }

    @Override
    public void renderBackground(Renderer renderer) {
        super.renderBackground(renderer);

//        font.draw(renderer, TextObject.translation("bubbles/saves/create/title"), 36, width / 2f, height / 2f - 30, Thickness.BOLD, Anchor.S);
//        font.draw(renderer, TextObject.translation("bubbles/saves/create/seed"), 36, width / 2f - 20, height / 2f + 15, Thickness.BOLD, Anchor.E);
//        font.draw(renderer, TextObject.translation("bubbles/saves/create/gamemode"), 36, width / 2f - 20, height / 2f + 55, Thickness.BOLD, Anchor.E);
    }

    public OptionsTextEntry getSeedEntry() {
        return seedEntry;
    }

    public ObjectList<Gamemode> getGamemodeList() {
        return gamemodeList;
    }

    public OptionsButton getCreateBtn() {
        return createBtn;
    }
}
