package com.ultreon.bubbles.render.screen;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.event.v1.SubscribeEvent;
import com.ultreon.bubbles.event.v1.TickEvent;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.save.SaveLoader;
import com.ultreon.bubbles.util.Util;
import com.ultreon.commons.annotation.FieldsAreNonnullByDefault;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
@SuppressWarnings({"FieldCanBeLocal", "unused", "CommentedOutCode"})
public class SavesScreen extends Screen {
    @Nullable
    private static SavesScreen instance;
    private final Collection<Supplier<GameSave>> saves;
    @Nullable
    private GameSave selectedSave;
    @Nullable
    private Screen backScreen;

    public SavesScreen(Screen backScreen) {
        super();

        SavesScreen.instance = this;

        // Configure back scene.
        this.backScreen = backScreen;

        // Logging.
        BubbleBlaster.getLogger().info("Initializing SavesScene");

        this.saves = SaveLoader.instance().getSaves();
    }

    @SuppressWarnings("EmptyMethod")
    private void newSave() {
//        Objects.requireNonNull(Util.getSceneManager()).displayScene(new CreateSaveScene(this));
    }

    private void openSave() {
        if (selectedSave != null) {
            BubbleBlaster.getInstance().loadSave(selectedSave);
        }
    }

    @Nullable
    public static SavesScreen instance() {
        return instance;
    }

    private void showLanguages() {
        Objects.requireNonNull(Util.getSceneManager()).displayScreen(new LanguageScreen(this));
    }

    private void back() {
        Objects.requireNonNull(Util.getSceneManager()).displayScreen(backScreen);
    }

    @Override
    public void init() {
        BubbleBlaster.getEventBus().subscribe(this);

//        panel.setVisible(true);

//        languageButton.bindEvents();
//        cancelButton.bindEvents();
//        scrollPane.validate();
    }

    @Override
    public boolean onClose(Screen to) {
        BubbleBlaster.getEventBus().unsubscribe(this);

//        panel.setVisible(false);

//        languageButton.unbindEvents();
//        cancelButton.unbindEvents();

        if (to == backScreen) {
            backScreen = null;
        }

//        scrollPane.invalidate();
        return super.onClose(to);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
//        languageButton.setX((int) Game.getMiddleX() - 322);
//        languageButton.setY((int) Game.getMiddleY() + 101);

//        cancelButton.setX((int) Game.getMiddleX() - 322);
//        cancelButton.setY((int) Game.getMiddleY() + 151);

//        if (evt.getPriority() == RenderEventPriority.BACKGROUND) {
//        }

//        if (evt.getPriority() == RenderEventPriority.FOREGROUND) {
//        }

        renderBackground(game, renderer);

//        cancelButton.setText(I18n.translateToLocal("other.cancel"));
//        cancelButton.render(game, renderer);

//        languageButton.setText(I18n.translateToLocal("scene.BubbleBlaster.options.language"));
//        languageButton.render(game, renderer);

//        scrollPane.setPreferredSize(new Dimension(800, BubbleBlaster.getInstance().getScaledHeight()));
//        scrollPane.setSize(new Dimension(800, BubbleBlaster.getInstance().getScaledHeight()));
//        panel.setPreferredSize(new Dimension(800, BubbleBlaster.getInstance().getScaledHeight()));
//        panel.setSize(new Dimension(800, BubbleBlaster.getInstance().getScaledHeight()));
//        scrollPane.setLocation(Game.instance().getWidth() / 2 - 300, 0);

//        scrollPane.paintAll(renderer.create(Game.instance().getWidth() / 2 - 400, 0, 800, Game.instance().getHeight()));
//        scrollPane.repaint(0);
//        scrollPane.addNotify();
//        scrollPane.invalidate();

//        Game.instance().getWindow().repaint();
//        Game.instance().getWindow().revalidate();

//        scrollPane.setVisible(true);

//        panel.revalidate();
//        scrollPane.repaint();
//        panel.repaint(0);
//        scrollPane.repaint(0);
//        panel.revalidate();
//        scrollPane.revalidate();

////        savesDisplay.paint(renderer);
////        savesDisplay.paintComponents(renderer);
////        savesDisplay.paintAll(renderer);
//        savesDisplay.paint(renderer.create(Game.instance().getWidth() / 2 - 400, 0, 800, Game.instance().getHeight()));
//        savesDisplay.repaint(0);
    }

    public void renderBackground(BubbleBlaster game, Renderer renderer) {
        renderer.color(new Color(96, 96, 96));
        renderer.rect(0, 0, game.getWidth(), game.getHeight());
    }

    @SuppressWarnings("EmptyMethod")
    @SubscribeEvent
    public void onUpdate(TickEvent evt) {
    }
}
