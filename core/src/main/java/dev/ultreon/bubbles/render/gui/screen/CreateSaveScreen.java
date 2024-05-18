package dev.ultreon.bubbles.render.gui.screen;

import dev.ultreon.bubbles.common.Difficulty;
import dev.ultreon.bubbles.gamemode.Gamemode;
import dev.ultreon.bubbles.init.Fonts;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Insets;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.widget.Button;
import dev.ultreon.bubbles.render.gui.widget.Label;
import dev.ultreon.bubbles.render.gui.widget.ObjectList;
import dev.ultreon.bubbles.render.gui.widget.TextEntry;
import dev.ultreon.libs.text.v1.TextObject;

import java.util.List;

public class CreateSaveScreen extends Screen {
    private final List<Gamemode> gamemodes;
    private final List<Difficulty> difficulties;
    private TextEntry seedEntry;
    private ObjectList<Gamemode> gamemodeList;
    private ObjectList<Difficulty> difficultyList;
    private Button createBtn;

    public CreateSaveScreen(Screen back) {
        this.gamemodes = List.copyOf(Registries.GAMEMODES.values());
        this.difficulties = List.of(Difficulty.values());
        this.setBackScreen(back);
    }

    @Override
    public void init() {
        var label = this.add(new Label(TextObject.translation("bubbleblaster.saves.create.title"), this.width / 2 - 150, 0, 300, 50));
        label.setFontSize(36);

        var label1 = this.add(new Label(TextObject.translation("bubbleblaster.saves.create.seed"), this.width / 2 - 150, 50, 300, 30));
        label1.setFontSize(20);

        var label2 = this.add(new Label(TextObject.translation("bubbleblaster.saves.create.gamemode"), this.width / 2 - 150, 95, 300, 25));
        label2.setFontSize(20);

        var startY = 120;

        this.seedEntry = this.add(new TextEntry.Builder().bounds(this.width / 2 - 150, startY, 300, 45).entryWidth(300).build());
        this.gamemodeList = this.add(new ObjectList<>(this.gamemodes, 30, 2, this.width / 2 - 150, startY + 50, 300, 195));
        this.gamemodeList.setSelectable(true);
        this.gamemodeList.setEntryRenderer(this::renderEntry);
        this.difficultyList = this.add(new ObjectList<>(this.difficulties, 30, 2, this.width / 2 - 150, startY + 250, 300, 195));
        this.difficultyList.setSelectable(true);
        this.difficultyList.setEntryRenderer(this::renderEntry);
        this.createBtn = this.add(Button.builder()
                .bounds(this.width / 2 - 50, startY + 450, 200, 40)
                .text(TextObject.translation("bubbleblaster.screen.saves.create.button"))
                .command(this::create)
                .build());
    }

    private void renderEntry(Renderer renderer, float width, float height, float y, Gamemode gamemode, boolean selected, boolean hovered) {
        var x = this.gamemodeList.getX();

        renderer.fill(x, y, width, height, Color.WHITE.withAlpha(hovered ? 0x40 : 0x20));
        renderer.drawTextLeft(this.font, gamemode.getName(), x + 10, y + height / 2f - 4, Color.WHITE.withAlpha(0xc0));

        if (selected)
            renderer.drawEffectBox(x, y, width, height, new Insets(1, 1, 4, 1));
        else if (hovered)
            renderer.drawEffectBox(x, y, width, height, new Insets(1, 1, 1, 1));
    }

    private void renderEntry(Renderer renderer, float width, float height, float y, Difficulty difficulty, boolean selected, boolean hovered) {
        var x = this.difficultyList.getX();

        renderer.fill(x, y, width, height, Color.WHITE.withAlpha(hovered ? 0x40 : 0x20));
        renderer.drawTextLeft(this.font, difficulty.getTranslation(), x + 10, y + height / 2f - 4, Color.WHITE.withAlpha(0xc0));

        if (selected)
            renderer.drawEffectBox(x, y, width, height, new Insets(1, 1, 4, 1));
        else if (hovered)
            renderer.drawEffectBox(x, y, width, height, new Insets(1, 1, 1, 1));
    }

    private void create() {
        var gamemode = this.gamemodeList.getSelected();
        if (gamemode == null) {
            return;
        }

        var difficulty = this.difficultyList.getSelected();
        if (difficulty == null) {
            return;
        }

        var seed = this.game.serializeSeed(this.seedEntry.getText());
        this.game.createGame(seed, gamemode.value, difficulty.value);
    }

    @Override
    public void renderBackground(Renderer renderer) {
        super.renderBackground(renderer);

        renderer.drawTextCenter(Fonts.SANS_HEADER_1.get(), TextObject.translation("bubbleblaster.screen.saves.create.title"), this.width / 2f, 60, Color.WHITE.withAlpha(0x80));
        renderer.drawTextRight(Fonts.SANS_HEADER_2.get(), TextObject.translation("bubbleblaster.screen.saves.create.seed"), this.width / 2f - 160, this.seedEntry.getY() + 24, Color.WHITE.withAlpha(0x80));
        renderer.drawTextRight(Fonts.SANS_HEADER_2.get(), TextObject.translation("bubbleblaster.screen.saves.create.gamemode"), this.width / 2f - 160, this.gamemodeList.getY() + 24, Color.WHITE.withAlpha(0x80));
        renderer.drawTextRight(Fonts.SANS_HEADER_2.get(), TextObject.translation("bubbleblaster.screen.saves.create.difficulty"), this.width / 2f - 160, this.difficultyList.getY() + 24, Color.WHITE.withAlpha(0x80));
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
