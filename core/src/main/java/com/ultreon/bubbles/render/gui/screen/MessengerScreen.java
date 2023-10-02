package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.commons.v0.MessengerImpl;

/**
 * Messenger screen, a screen that shows a message that can change. Useful for loading something like a dimension or a world.
 *
 * @author XyperCode
 * @since 0.0.0
 */
public class MessengerScreen extends Screen {
    private final Messenger messenger = new MessengerImpl(this::setDescription) {
    };
    private String description = "";
    protected BubbleBlaster game = BubbleBlaster.getInstance();

    public MessengerScreen() {

    }

    public MessengerScreen(String description) {
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void init() {

    }

    public Messenger getMessenger() {
        return this.messenger;
    }

    /**
     * Renders the world loading scene.<br>
     * Shows the title in the blue accent color (#00b0ff), and the description in a 50% black color (#7f7f7f).
     *
     * @param game      the game launched.
     * @param renderer  the graphics 2D processor.
     * @param deltaTime game frame time.
     */
    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.fill(0, 0, game.getWidth(), game.getHeight(), Color.grayscale(0x40));

        renderer.drawTextCenter(Fonts.SANS_REGULAR_48.get(), "Loading World...", BubbleBlaster.getMiddleX(), game.getHeight() / 2f + 8, Color.rgb(0x00c0ff));
        renderer.drawTextCenter(Fonts.SANS_REGULAR_20.get(), this.description, BubbleBlaster.getMiddleX(), game.getHeight() / 2f + 65f, Color.rgb(0x7f7f7f));
    }

    public String getDescription() {
        return this.description;
    }
}
