package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.Gdx;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.TitleButton;
import com.ultreon.libs.text.v1.TextObject;
import net.fabricmc.loader.api.FabricLoader;

public class TitleScreen extends Screen {

    public TitleScreen() {

    }

    private void openSavesSelection() {
        this.game.showScreen(new SavesScreen(this));
    }

    private void openModList() {
        this.game.showScreen(new ModListScreen());
    }

    private void openLanguageSettings() {
        this.game.showScreen(new LanguageScreen(this));
    }

    private void openOptions() {
        this.game.showScreen(new OptionsScreen(this));
    }

    private void startGame() {
        this.game.showScreen(new StartOptionsScreen(this));
    }

    @Override
    public void init() {
        this.clearWidgets();

        if (this.game.menuMusic.isStopped() || this.game.menuMusic.isPaused()) {
            this.game.menuMusic.play();
        }

        this.add(new TitleButton.Builder()
                .bounds(this.width / 2 - 200, 220, 400, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.start"))
                .command(this::startGame)
                .build());
        this.add(new TitleButton.Builder()
                .bounds(this.width / 2 - 200, 300, 400, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.saves"))
                .command(this::openSavesSelection)
                .build());
        this.add(new TitleButton.Builder()
                .bounds(this.width / 2 - 200, 380, 190, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.mods"))
                .command(this::openModList)
                .build());
        this.add(new TitleButton.Builder()
                .bounds(this.width / 2 + 10, 380, 190, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.options"))
                .command(this::openOptions)
                .build());
        this.add(new TitleButton.Builder()
                .bounds(this.width / 2 - 200, 460, 190, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.language"))
                .command(this::openLanguageSettings)
                .build());
        this.add(new TitleButton.Builder()
                .bounds(this.width / 2 + 10, 460, 190, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.quit"))
                .command(this.game::shutdown)
                .build());
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.renderBackground(renderer);

        renderer.fill(BubbleBlaster.getInstance().getGameBounds(), Color.rgb(0x404040));

        renderer.fill(0, 0, this.width, 175, Color.grayscale(0x1e));

        renderer.fillEffect(0, 175, this.width, 3);
        renderer.fillGradient(0, 178, this.width, 20, Color.argb(0x20000000), Color.TRANSPARENT);

        renderer.drawTextCenter(Fonts.DONGLE_140.get(), "Bubble Blaster", this.width / 2f, 87.5f, Color.WHITE);

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
