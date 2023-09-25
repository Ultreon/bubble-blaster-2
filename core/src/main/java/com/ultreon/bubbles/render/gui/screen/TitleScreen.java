package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.libs.commons.v0.Anchor;
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
                .text(TextObject.translation("bubbles/screen/title/start"))
                .command(this::startGame)
                .build());
        savesButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 300, 400, 60)
                .text(TextObject.translation("bubbles/screen/title/saves"))
                .command(this::openSavesSelection)
                .build());
        modsButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 380, 190, 60)
                .text(TextObject.translation("bubbles/screen/title/mods"))
                .command(this::openModList)
                .build());
        optionsButton = add(new TitleButton.Builder()
                .bounds(width / 2 + 10, 380, 190, 60)
                .text(TextObject.translation("bubbles/screen/title/options"))
                .command(this::openOptions)
                .build());
        languageButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 460, 190, 60)
                .text(TextObject.translation("bubbles/screen/title/language"))
                .command(this::openLanguageSettings)
                .build());
        quitButton = add(new TitleButton.Builder()
                .bounds(width / 2 + 10, 460, 190, 60)
                .text(TextObject.translation("bubbles/screen/title/quit"))
                .command(game::shutdown)
                .build());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        ticks += partialTicks;
        renderer.setColor(0xff404040);
        renderer.fill(BubbleBlaster.getInstance().getGameBounds());

        renderer.setColor(0xff1e1e1e);
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), 175);

        float shiftX = (float) BubbleBlaster.getInstance().getWidth() * 2f * ticks / ((float) BubbleBlaster.TPS * 10f);

        renderer.fillEffect(0, 175, BubbleBlaster.getInstance().getWidth(), 3);

        renderer.setColor(0xffffffff);
        renderer.drawText(Fonts.DONGLE_140.get(), "Bubble Blaster", (float) BubbleBlaster.getInstance().getWidth() / 2, 87, Anchor.S);

        renderer.setColor(0xffffffff);
        renderer.drawText(monospaced, "Game Version: " + BubbleBlaster.getGameVersion().getFriendlyString(), 10, 10);
        renderer.drawText(monospaced, "Loader Version: " + BubbleBlaster.getFabricLoaderVersion().getFriendlyString(), 10, 22);
        renderer.drawText(monospaced, "Mods Loaded: " + FabricLoader.getInstance().getAllMods().size(), 10, 34);
        renderer.drawText(monospaced, "High Score: " + (int)game.getGlobalData().getHighScore(), 10, 46);

        this.renderChildren(renderer);
    }
}
