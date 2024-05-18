package dev.ultreon.bubbles.render.gui.screen.options;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.BubbleBlasterConfig;
import dev.ultreon.bubbles.render.Color;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.screen.Screen;
import dev.ultreon.bubbles.render.gui.widget.Button;
import dev.ultreon.bubbles.text.Translations;
import dev.ultreon.libs.text.v1.TextObject;

public class OptionsScreen extends Screen {
    private Button generic;
    private Button gameplay;
    private Button graphical;
    private Button language;
    private Button cancelButton;
    private Button saveButton;

    public OptionsScreen() {
        super();
    }

    private void save() {
        BubbleBlasterConfig.save();
        this.back();
    }

    private void showGeneric() {
        this.game.showScreen(new GenericOptionsScreen(this));
    }

    private void showGameplay() {
        this.game.showScreen(new GameplayOptionsScreen(this));
    }

    private void showGraphical() {
        this.game.showScreen(new GraphicalOptionsScreen(this));
    }

    private void showLanguages() {
        this.game.showScreen(new LanguageScreen());
    }

    @Override
    public void init() {
        this.generic = this.add(Button.builder()
                .text(TextObject.translation("bubbleblaster.screen.options.generic"))
                .bounds(this.middleX - 251, this.middleY + 51, 250, 48)
                .command(this::showGeneric).build());
        this.gameplay = this.add(Button.builder()
                .text(TextObject.translation("bubbleblaster.screen.options.gameplay"))
                .bounds(this.middleX + 1, this.middleY + 51, 250, 48)
                .command(this::showGameplay).build());
        this.graphical = this.add(Button.builder()
                .text(TextObject.translation("bubbleblaster.screen.options.graphical"))
                .bounds(this.middleX - 251, this.middleY + 101, 250, 48)
                .command(this::showGraphical).build());
        this.language = this.add(Button.builder()
                .text(TextObject.translation("bubbleblaster.screen.options.language"))
                .bounds(this.middleX + 1, this.middleY + 101, 250, 48)
                .command(this::showLanguages).build());
        this.cancelButton = this.add(Button.builder().text(Translations.CANCEL).bounds(this.middleX - 151, this.middleY + 151, 150, 48).command(this::back).build());
        this.saveButton = this.add(Button.builder().text(Translations.SAVE).bounds(this.middleX + 1, this.middleY + 151, 150, 48).command(this::save).build());
    }

    public void renderBackground(BubbleBlaster game, Renderer renderer) {
        renderer.fill(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight(), Color.GRAY_6);
    }

    public Button getGeneric() {
        return this.generic;
    }

    public Button getGameplay() {
        return this.gameplay;
    }

    public Button getGraphical() {
        return this.graphical;
    }

    public Button getLanguage() {
        return this.language;
    }

    public Button getCancelButton() {
        return this.cancelButton;
    }

    public Button getSaveButton() {
        return this.saveButton;
    }
}
