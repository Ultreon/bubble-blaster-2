package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GamePlatform;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.TitleScreenBackground;
import com.ultreon.bubbles.render.gui.screen.options.LanguageScreen;
import com.ultreon.bubbles.render.gui.screen.options.OptionsScreen;
import com.ultreon.bubbles.render.gui.widget.Button;
import com.ultreon.libs.text.v1.TextObject;

import java.time.Instant;

public class TitleScreen extends Screen {

    private TitleScreenBackground background;

    public TitleScreen() {

    }

    private void openSavesSelection() {
        this.game.showScreen(new SavesScreen(this));
    }

    private void openModList() {
        Screen screen = GamePlatform.get().openModListScreen();
        if (screen == null) {
            this.game.notifications.unavailable("Mods on " + GamePlatform.get().getOperatingSystem().toString());
            return;
        }
        this.game.showScreen(screen);
    }

    private void openLanguageSettings() {
        this.game.showScreen(new LanguageScreen());
    }

    private void openOptions() {
        this.game.showScreen(new OptionsScreen());
    }

    private void startGame() {
        this.game.showScreen(new StartOptionsScreen(this));
    }

    @Override
    public void init() {
        if (this.background != null) {
            this.background.dispose();
        }

        this.clearWidgets();

        this.background = new TitleScreenBackground(this.width, this.height);

        if (this.game.menuMusic.isStopped() || this.game.menuMusic.isPaused()) {
            this.game.menuMusic.play();
        }

        this.add(Button.builder()
                .bounds(this.width / 2 - 200, 220, 400, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.start"))
                .command(this::startGame)
                .font(Fonts.SANS_REGULAR_20.get())
                .build());
        this.add(Button.builder()
                .bounds(this.width / 2 - 200, 300, 400, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.saves"))
                .command(this::openSavesSelection)
                .font(Fonts.SANS_REGULAR_20.get())
                .build());
        this.add(Button.builder()
                .bounds(this.width / 2 - 200, 380, 190, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.mods"))
                .command(this::openModList)
                .font(Fonts.SANS_REGULAR_20.get())
                .build());
        this.add(Button.builder()
                .bounds(this.width / 2 + 10, 380, 190, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.options"))
                .command(this::openOptions)
                .font(Fonts.SANS_REGULAR_20.get())
                .build());
        this.add(Button.builder()
                .bounds(this.width / 2 - 200, 460, 190, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.language"))
                .command(this::openLanguageSettings)
                .font(Fonts.SANS_REGULAR_20.get())
                .build());
        this.add(Button.builder()
                .bounds(this.width / 2 + 10, 460, 190, 60)
                .text(TextObject.translation("bubbleblaster.screen.title.quit"))
                .command(this.game::shutdown)
                .font(Fonts.SANS_REGULAR_20.get())
                .build());
        this.add(Button.builder()
                .bounds(this.width - 40 - 180, 100, 180, 40)
                .text(TextObject.translation("bubbleblaster.screen.title.resetHighScore"))
                .command(this::resetHighScore)
                .font(Fonts.SANS_REGULAR_20.get())
                .build());
    }

    private void resetHighScore() {
        GlobalSaveData globalData = this.game.getGlobalData();
        globalData.setHighScore(0.0, Instant.EPOCH);
    }

    @Override
    public void tick() {
        super.tick();

        TitleScreenBackground background = this.background;
        if (background != null) {
            background.tick();
        }
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.background.render(renderer);

        renderer.fill(0, 0, this.width, 175, Color.grayscale(0x1e));

        renderer.fillEffect(0, 175, this.width, 3);
        renderer.fillGradient(0, 178, this.width, 20, Color.argb(0x20000000), Color.TRANSPARENT);

        renderer.drawTextCenter(Fonts.DONGLE_140.get(), "Bubble Blaster", this.width / 2f, 87.5f, Color.WHITE);

        renderer.drawText(this.monospaced, "Game Version: " + BubbleBlaster.getGameVersion(), 40, 40, Color.WHITE);
        renderer.drawText(this.monospaced, "LibGDX Version: " + BubbleBlaster.getLibGDXVersion(), 40, 52, Color.WHITE);

        if (GamePlatform.get().allowsMods()) {
            renderer.drawText(this.monospaced, "Loader Version: " + BubbleBlaster.getFabricLoaderVersion(), 40, 64, Color.WHITE);
            renderer.drawText(this.monospaced, "Mods Loaded: " + GamePlatform.get().getModsCount(), 40, 76, Color.WHITE);
        }

        renderer.drawTextRight(Fonts.SANS_BOLD_32.get(), "High Score", this.width - 40, 40, Color.WHITE);
        renderer.drawTextRight(Fonts.SANS_REGULAR_16.get(), String.valueOf(Math.round(game.getGlobalData().getHighScore())), this.width - 40, 80, Color.WHITE);

        this.renderChildren(renderer, mouseX, mouseY, deltaTime);
    }

    @Override
    public void renderCloseButton(Renderer renderer, int mouseX, int mouseY) {

    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        return super.mousePress(x, y, button);
    }

    @Override
    public boolean close(Screen to) {
        if (to instanceof TitleScreen) return true;
        return super.close(to);
    }

    @Override
    public boolean keyPress(int keyCode) {
        return false;
    }
}
