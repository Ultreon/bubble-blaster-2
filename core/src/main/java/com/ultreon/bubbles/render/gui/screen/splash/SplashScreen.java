package com.ultreon.bubbles.render.gui.screen.splash;

import com.badlogic.gdx.graphics.Texture;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.media.SoundInstance;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.screen.LoadScreen;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.util.Utils;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.libs.commons.v0.vector.Vec2f;

public class SplashScreen extends Screen {
    private static final float DURATION = 6000f;
    private static final float FROM_ZOOM = 2.0f;
    private static final float TO_ZOOM = 1.3f;
    private static final int TEXTURE_WIDTH = 3840;
    private static final int TEXTURE_HEIGHT = 2160;
    private static final int FADE_OUT = 1000;
    private static final int FADE_IN = 1000;
    private Texture logoTexture;
    private double zoom;
    private long startTime = -1L;
    private Resizer resizer;
    private boolean ended;

    //from https://www.java2s.com
    public static double interpolate(double a, double b, double d) {
        return a + (b - a) * d;
    }

    @Override
    public void init() {
//        try (InputStream stream = BubbleBlaster.getGameJar().openStream("assets/bubbles/logo.png")) {
//            this.logoImage = ImageIO.read(stream);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        logoTexture = new Texture("assets/bubbles/logo.png");

        BubbleBlaster.getInstance().startLoading();
        Utils.hideCursor();

        this.resizer = new Resizer(TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        if (this.startTime == -1L) {
            this.startTime = System.currentTimeMillis();

            SoundInstance ambientAudio;
            ambientAudio = new SoundInstance(BubbleBlaster.id("sfx/logo_reveal"));
            ambientAudio.setVolume(0.4f);
            ambientAudio.play();
        }

        final long timeDiff = System.currentTimeMillis() - this.startTime;
        float zoom = (float) interpolate(FROM_ZOOM, TO_ZOOM, Mth.clamp(timeDiff / DURATION, 0f, 1f));
        Vec2f thumbnail = this.resizer.thumbnail(this.width * zoom, this.height * zoom);
        this.zoom = zoom;

        float drawWidth = thumbnail.x;
        float drawHeight = thumbnail.y;

        float drawX = (this.width - drawWidth) / 2;
        float drawY = (this.height - drawHeight) / 2;

        renderer.blit(this.logoTexture, (int) drawX, (int) drawY, (int) drawWidth, (int) drawHeight, LoadScreen.BACKGROUND);

        if (timeDiff >= DURATION - FADE_OUT) {
            int clamp = (int) Mth.clamp(255 * (timeDiff - DURATION + FADE_OUT) / FADE_OUT, 0, 255);
            Color color = Color.rgba(0, 0, 0, clamp);
            GuiComponent.fill(renderer, 0, 0, width, height, color);
        }

//        if (timeDiff <= FADE_IN) {
//            int clamp = (int) MathHelper.clamp(255 * (1f - ((float)timeDiff) / FADE_IN), 0, 255);
//            Color color = new Color(0, 0, 0, clamp);
//            GuiElement.fill(renderer, 0, 0, width, height, color);
//        }

        if (timeDiff >= DURATION) {
            this.ended = true;
            Utils.showCursor();
            game.showScreen(new LoadScreen(), true);
            game.fadeIn(1000f);
        }
    }

    public double getZoom() {
        return zoom;
    }

    @Override
    public boolean onClose(Screen to) {
        return ended;
    }
}
