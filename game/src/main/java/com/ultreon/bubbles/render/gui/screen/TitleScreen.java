package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.common.text.TranslationText;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.render.gui.widget.TitleButton;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;

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
                .text(new TranslationText("bubbles/screen/title/start"))
                .command(this::startGame)
                .build());
        savesButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 300, 400, 60)
                .text(new TranslationText("bubbles/screen/title/saves"))
                .command(this::openSavesSelection)
                .build());
        modsButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 380, 190, 60)
                .text(new TranslationText("bubbles/screen/title/mods"))
                .command(this::openModList)
                .build());
        optionsButton = add(new TitleButton.Builder()
                .bounds(width / 2 + 10, 380, 190, 60)
                .text(new TranslationText("bubbles/screen/title/options"))
                .command(this::openOptions)
                .build());
        languageButton = add(new TitleButton.Builder()
                .bounds(width / 2 - 200, 460, 190, 60)
                .text(new TranslationText("bubbles/screen/title/language"))
                .command(this::openLanguageSettings)
                .build());
        quitButton = add(new TitleButton.Builder()
                .bounds(width / 2 + 10, 460, 190, 60)
                .text(new TranslationText("bubbles/screen/title/quit"))
                .command(game::shutdown)
                .build());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        ticks += partialTicks;
        renderer.color(0xff404040);
        renderer.fill(BubbleBlaster.getInstance().getGameBounds());

        renderer.color(0xff1e1e1e);
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), 175);

        float shiftX = (float) BubbleBlaster.getInstance().getWidth() * 2f * ticks / ((float) BubbleBlaster.TPS * 10f);

        renderer.fillEffect(0, 175, BubbleBlaster.getInstance().getWidth(), 3);

        renderer.color(0xffffffff);
        Fonts.DONGLE.draw(renderer, "Bubble Blaster", 140, (float) BubbleBlaster.getInstance().getWidth() / 2, 87, Thickness.BOLD, Anchor.S);

        renderer.color(0xffffffff);
        monospaced.draw(renderer, "Game Version: " + BubbleBlaster.getGameVersion().getFriendlyString(), 11, 10, 10, Thickness.BOLD);
        monospaced.draw(renderer, "Loader Version: " + BubbleBlaster.getFabricLoaderVersion().getFriendlyString(), 11, 10, 22, Thickness.BOLD);
        monospaced.draw(renderer, "Mods Loaded: " + FabricLoader.getInstance().getAllMods().size(), 11, 10, 34, Thickness.BOLD);
        monospaced.draw(renderer, "High Score: " + (int)game.getGlobalData().getHighScore(), 11, 10, 46, Thickness.BOLD);

        this.renderChildren(renderer);
    }
}
