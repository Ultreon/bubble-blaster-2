package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.common.text.TranslationText;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.TitleButton;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.vector.Vec2i;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("FieldCanBeLocal")
public class TitleScreen extends Screen {
    private static final Font INFO_FONT = new Font(BubbleBlaster.getInstance().getMonospaceFontName(), Font.PLAIN, 11);
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated(since = "0.0.3213", forRemoval = true)
    private static TitleScreen instance;
    private TitleButton startButton;
    private TitleButton languageButton;
    private TitleButton savesButton;
    private TitleButton optionsButton;
    private float ticks;

    public TitleScreen() {
        instance = this;
    }

    private void openSavesSelection() {
        ScreenManager screenManager = Util.getSceneManager();
        screenManager.displayScreen(new SavesScreen(this));
    }

    private void openModList() {
        game.showScreen(new ModListScreen());
    }

    @Deprecated(since = "0.0.3213", forRemoval = true)
    public static TitleScreen instance() {
        return instance;
    }

    private void openLanguageSettings() {
        ScreenManager screenManager = Util.getSceneManager();
        screenManager.displayScreen(new LanguageScreen(this));
    }

    private void openOptions() {
        ScreenManager screenManager = Util.getSceneManager();
        screenManager.displayScreen(new OptionsScreen(this));
    }

    private void startGame() {
        game.showScreen(new StartOptionsScreen(this));
    }

    @Override
    public void init() {
        clearWidgets();

        BubbleBlaster.getInstance().updateRPC();

        startButton = add(new TitleButton.Builder()
                .bounds(0, 220, 225, 48)
                .text(new TranslationText("bubbles/screen/title/start"))
                .command(this::startGame)
                .build());
        savesButton = add(new TitleButton.Builder()
                .bounds(0, 280, 200, 48)
                .text(new TranslationText("bubbles/screen/title/saves"))
                .command(this::openSavesSelection)
                .build());
        savesButton = add(new TitleButton.Builder()
                .bounds(0, 340, 200, 48)
                .text(new TranslationText("bubbles/screen/title/mods"))
                .command(this::openModList)
                .build());
        optionsButton = add(new TitleButton.Builder()
                .bounds(BubbleBlaster.getInstance().getWidth() - 225, 220, 225, 48)
                .text(new TranslationText("bubbles/screen/title/options"))
                .command(this::openOptions)
                .build());
        languageButton = add(new TitleButton.Builder()
                .bounds(BubbleBlaster.getInstance().getWidth() - 200, 280, 200, 48)
                .text(new TranslationText("bubbles/screen/title/language"))
                .command(this::openLanguageSettings)
                .build());

        startButton.setX(0);
        savesButton.setX(0);
        optionsButton.setX(BubbleBlaster.getInstance().getWidth() - 225);
        languageButton.setX(BubbleBlaster.getInstance().getWidth() - 200);

        startButton.make();
        savesButton.make();
        optionsButton.make();
        languageButton.make();
    }

    @Override
    public boolean onClose(Screen to) {
        startButton.destroy();
        savesButton.destroy();
        optionsButton.destroy();
        languageButton.destroy();
        return super.onClose(to);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        ticks += partialTicks;
        renderer.color(0xff808080);
        renderer.fill(BubbleBlaster.getInstance().getGameBounds());

        renderer.color(0xff404040);
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), 175);

        float shiftX = (float) BubbleBlaster.getInstance().getWidth() * 2f * ticks / ((float) BubbleBlaster.TPS * 10f);

        GradientPaint p = new GradientPaint(shiftX - BubbleBlaster.getInstance().getWidth(), 0f, new Color(0, 192, 255), shiftX, 0f, new Color(0, 255, 192), true);
        renderer.paint(p);
        renderer.rect(0, 175, BubbleBlaster.getInstance().getWidth(), 3);

        renderer.color(0xffffffff);
        GraphicsUtils.drawCenteredString(renderer, "Bubble Blaster", new Rectangle2D.Double(0, 0, BubbleBlaster.getInstance().getWidth(), 145), new Font(BubbleBlaster.getInstance().getGameFont().getFontName(), Font.PLAIN, 87));

        renderer.font(INFO_FONT);
        renderer.color(0xffffffff);
        GraphicsUtils.drawLeftAnchoredString(renderer, "Game Version: " + BubbleBlaster.getGameVersion().getFriendlyString(), new Vec2i(10, 10), 12, INFO_FONT);
        GraphicsUtils.drawLeftAnchoredString(renderer, "Loader Version: " + BubbleBlaster.getFabricLoaderVersion().getFriendlyString(), new Vec2i(10, 22), 12, INFO_FONT);
        GraphicsUtils.drawLeftAnchoredString(renderer, "Mods Loaded: " + FabricLoader.getInstance().getAllMods().size(), new Vec2i(10, 34), 12, INFO_FONT);

        this.renderChildren(renderer);
    }
}
