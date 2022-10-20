package com.ultreon.bubbles.render.screen.splash;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.media.Sound;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.LoadScreen;
import com.ultreon.bubbles.render.screen.Screen;
import com.ultreon.bubbles.render.screen.gui.GuiElement;
import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.bubbles.vector.Vec2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class SplashScreen extends Screen {
    private static final float DURATION = 6000f;
    private static final float FROM_ZOOM = 2.0f;
    private static final float TO_ZOOM = 1.3f;
    private static final int TEXTURE_WIDTH = 3840;
    private static final int TEXTURE_HEIGHT = 2160;
    private static final int FADE_OUT = 1000;
    private static final int FADE_IN = 1000;
    private BufferedImage logoImage;
    private double zoom;
    private long startTime;
    private Resizer resizer;

    //from https://www.java2s.com
    public static double interpolate(double a, double b, double d) {
        return a + (b - a) * d;
    }

    @Override
    public void init() {
        try {
            this.logoImage = ImageIO.read(Objects.requireNonNull(BubbleBlaster.class.getResourceAsStream("/logo.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BubbleBlaster.getInstance().startLoading();
        BubbleBlaster.getInstance().getGameWindow().setCursor(BubbleBlaster.getInstance().getBlankCursor());

        this.resizer = new Resizer(TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        if (this.startTime == 0L) {
            this.startTime = System.currentTimeMillis();

            Sound ambientAudio = null;
            try {
                ambientAudio = new Sound(Objects.requireNonNull(getClass().getResource("/assets/bubbles/audio/sfx/logo_reveal.mp3")), "ambient");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            ambientAudio.setVolume(0.4d);
            ambientAudio.play();
        }

        final long timeDiff = System.currentTimeMillis() - startTime;
        float zoom = (float) interpolate(FROM_ZOOM, TO_ZOOM, MathHelper.clamp(timeDiff / DURATION, 0f, 1f));
        Vec2f thumbnail = resizer.thumbnail(width * zoom, height * zoom);
        this.zoom = zoom;

        float drawWidth = thumbnail.x;
        float drawHeight = thumbnail.y;

        float drawX = (width - drawWidth) / 2;
        float drawY = (height - drawHeight) / 2;

        renderer.image(logoImage, (int) drawX, (int) drawY, (int) drawWidth, (int) drawHeight, LoadScreen.BACKGROUND);

        if (timeDiff >= DURATION - FADE_OUT) {
            int clamp = (int) MathHelper.clamp(255 * (timeDiff - DURATION + FADE_OUT) / FADE_OUT, 0, 255);
            Color color = new Color(0, 0, 0, clamp);
            GuiElement.fill(renderer, 0, 0, width, height, color);
        }

        if (timeDiff <= FADE_IN) {
            int clamp = (int) MathHelper.clamp(255 * (1f - ((float)timeDiff) / FADE_IN), 0, 255);
            Color color = new Color(0, 0, 0, clamp);
            GuiElement.fill(renderer, 0, 0, width, height, color);
        }

        if (timeDiff >= DURATION) {
            game.showScreen(new LoadScreen(), true);
            game.fadeIn(1000f);
        }
    }

    public double getZoom() {
        return zoom;
    }
}
