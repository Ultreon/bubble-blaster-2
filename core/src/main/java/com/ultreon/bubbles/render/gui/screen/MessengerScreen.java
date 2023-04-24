package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.commons.lang.Messenger;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Messenger screen, a screen that shows a message that can change. Useful for loading something like a dimension or a world.
 *
 * @author Qboi
 * @since 0.0.0
 */
public class MessengerScreen extends Screen {
    private final Messenger messenger = new Messenger(this::setDescription);
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
        return messenger;
    }

    /**
     * Renders the environment loading scene.<br>
     * Shows the title in the blue accent color (#00b0ff), and the description in a 50% black color (#7f7f7f).
     *
     * @param game         the game launched.
     * @param renderer     the graphics 2D processor.
     * @param partialTicks game frame time.
     */
    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        renderer.color(Color.rgb(0x404040));
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
        if (GameSettings.instance().getGraphicsSettings().isTextAntialiasEnabled())
            renderer.hint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        renderer.color(Color.rgb(0x00c0ff));
        GraphicsUtils.drawCenteredString(renderer, "Loading Environment...", new Rectangle2D.Double(0, ((double) BubbleBlaster.getInstance().getHeight() / 2) - 24, BubbleBlaster.getInstance().getWidth(), 64d), new Font("Helvetica", Font.PLAIN, 48));
        renderer.color(Color.rgb(0x7f7f7f));
        GraphicsUtils.drawCenteredString(renderer, this.description, new Rectangle2D.Double(0, ((double) BubbleBlaster.getInstance().getHeight() / 2) + 40, BubbleBlaster.getInstance().getWidth(), 50d), new Font("Helvetica", Font.PLAIN, 20));
        if (GameSettings.instance().getGraphicsSettings().isTextAntialiasEnabled())
            renderer.hint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public String getDescription() {
        return description;
    }
}
