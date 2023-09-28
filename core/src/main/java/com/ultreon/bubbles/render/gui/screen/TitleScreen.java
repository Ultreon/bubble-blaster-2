package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.Gdx;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.debug.Debug;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.TitleButton;
import com.ultreon.libs.text.v0.TextObject;
import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings("FieldCanBeLocal")
public class TitleScreen extends Screen {
    private TitleButton startButton;
    private TitleButton languageButton;
    private TitleButton savesButton;
    private TitleButton modsButton;
    private TitleButton optionsButton;
    private TitleButton quitButton;
    private float ticks;

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

        startButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 220, 400, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/start"))
                .command(this::startGame)
                .build());
        savesButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 300, 400, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/saves"))
                .command(this::openSavesSelection)
                .build());
        modsButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 380, 190, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/mods"))
                .command(this::openModList)
                .build());
        optionsButton = add(new TitleButton.Builder()
                .bounds(width / 2 + 10, 380, 190, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/options"))
                .command(this::openOptions)
                .build());
        languageButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 460, 190, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/language"))
                .command(this::openLanguageSettings)
                .build());
        quitButton = add(new TitleButton.Builder()
                .bounds(width / 2 + 10, 460, 190, 60)
                .text(TextObject.translation("bubbleblaster/screen/title/quit"))
                .command(game::shutdown)
                .build());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.renderBackground(renderer);

        ticks += deltaTime;
        renderer.setColor(0xff404040);
        renderer.fill(BubbleBlaster.getInstance().getGameBounds());

        renderer.setColor(0xff1e1e1e);
        renderer.rect(0, 0, game.getWidth(), 175);

        renderer.fillEffect(0, 175, game.getWidth(), 3);
        renderer.fillGradient(0, 178, game.getWidth(), 20, Color.argb(0x20000000), Color.argb(0x00000000));

        renderer.setColor(0xffffffff);
        renderer.drawCenteredText(Fonts.DONGLE_140.get(), "Bubble Blaster", Gdx.graphics.getWidth() / 2f, 87);

        renderer.setColor(0xffffffff);
        renderer.drawText(monospaced, "Game Version: " + BubbleBlaster.getGameVersion().getFriendlyString(), 20, 20);
        renderer.drawText(monospaced, "Loader Version: " + BubbleBlaster.getFabricLoaderVersion().getFriendlyString(), 20, 32);
        renderer.drawText(monospaced, "Mods Loaded: " + FabricLoader.getInstance().getAllMods().size(), 20, 44);
        renderer.drawText(monospaced, "High Score: " + (int)game.getGlobalData().getHighScore(), 20, 56);

        this.renderChildren(renderer, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        return super.mousePress(x, y, button);
    }
}
