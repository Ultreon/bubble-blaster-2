package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.TitleButton;
import com.ultreon.libs.text.v0.TextObject;
import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings("FieldCanBeLocal")
public class TitleScreen extends Screen {

    public TitleScreen() {

    }

    private void openSavesSelection() {
        game.showScreen(new SavesScreen(this));
    }

    private void openModList() {
        game.showScreen(new ModListScreen());
    }

    private void openLanguageSettings() {
        game.showScreen(new LanguageScreen(this));
    }

    private void openOptions() {
        game.showScreen(new OptionsScreen(this));
    }

    private void startGame() {
        game.showScreen(new StartOptionsScreen(this));
    }

    @Override
    public void init() {
        clearWidgets();

        BubbleBlaster.getInstance().updateRPC();

        add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 220, 400, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/start"))
                .command(this::startGame)
                .build());
        add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 300, 400, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/saves"))
                .command(this::openSavesSelection)
                .build());
        add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 380, 190, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/mods"))
                .command(this::openModList)
                .build());
        add(new TitleButton.Builder()
                .bounds(width / 2 + 10, 380, 190, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/options"))
                .command(this::openOptions)
                .build());
        add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 460, 190, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/language"))
                .command(this::openLanguageSettings)
                .build());
        add(new TitleButton.Builder()
                .bounds(width / 2 + 10, 460, 190, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/quit"))
                .command(game::shutdown)
                .build());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.renderBackground(renderer);

        renderer.fill(BubbleBlaster.getInstance().getGameBounds(), Color.rgb(0x404040));

        renderer.setColor(0xff1e1e1e);
        renderer.fill(0, 0, game.getWidth(), 175);

        renderer.fillEffect(0, 175, game.getWidth(), 3);
        renderer.fillGradient(0, 178, game.getWidth(), 20, Color.argb(0x20000000), Color.TRANSPARENT);

        renderer.setColor(0xffffffff);
        renderer.drawCenteredText(Fonts.DONGLE_140.get(), "Bubble Blaster", this.width / 2f, 87);

        renderer.drawText(this.monospaced, "Game Version: " + BubbleBlaster.getGameVersion().getFriendlyString(), 40, 40, Color.WHITE);
        renderer.drawText(this.monospaced, "Loader Version: " + BubbleBlaster.getFabricLoaderVersion().getFriendlyString(), 40, 52, Color.WHITE);
        renderer.drawText(this.monospaced, "Mods Loaded: " + FabricLoader.getInstance().getAllMods().size(), 40, 64, Color.WHITE);
        renderer.drawText(this.monospaced, "High Score: " + (int)game.getGlobalData().getHighScore(), 40, 76, Color.WHITE);

        this.renderChildren(renderer, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        return super.mousePress(x, y, button);
    }
}
