package dev.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.render.gui.Resizer;
import dev.ultreon.libs.commons.v0.Mth;

public final class SplashScreen extends InternalScreen {
    private static final float DURATION = 6000f;
    private static final float FROM_ZOOM = 2.0f;
    private static final float TO_ZOOM = 1.3f;
    private static final int TEXTURE_WIDTH = 3840;
    private static final int TEXTURE_HEIGHT = 2160;
    private static final int FADE_OUT = 1000;
    private static boolean initialized;
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
    public synchronized void init() {
        if (initialized) return;
        initialized = true;

        this.logoTexture = new Texture("assets/bubbleblaster/logo.png");

        BubbleBlaster.getInstance().startLoading();

        this.resizer = new Resizer(TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        if (this.startTime == -1L) {
            this.startTime = System.currentTimeMillis();

            var sound = Gdx.audio.newSound(Gdx.files.internal("assets/bubbleblaster/sounds/logo_reveal.mp3"));
            sound.play(0.4f);
        }

        final var timeDiff = System.currentTimeMillis() - this.startTime;
        var zoom = (float) SplashScreen.interpolate(FROM_ZOOM, TO_ZOOM, Mth.clamp(timeDiff / DURATION, 0f, 1f));
        var thumbnail = this.resizer.thumbnail(this.width * zoom, this.height * zoom);
        this.zoom = zoom;

        var drawWidth = thumbnail.x;
        var drawHeight = thumbnail.y;

        var drawX = (this.width - drawWidth) / 2;
        var drawY = (this.height - drawHeight) / 2;

        renderer.blit(this.logoTexture, (int) drawX, (int) drawY, (int) drawWidth, (int) drawHeight, LoadScreen.BACKGROUND);

        // Todo: fix fading
//        if (timeDiff >= DURATION - FADE_OUT) {
//            int clamp = (int) Mth.clamp(255 * (timeDiff - DURATION + FADE_OUT) / FADE_OUT, 0, 255);
//            Color color = Color.rgba(0, 0, 0, clamp);
//            renderer.fill(0, 0, this.width, this.height, color);
//        }

        if (timeDiff >= DURATION) {
            this.ended = true;
            game.showScreen(new LoadScreen(), true);
//            game.fadeIn(1000f);
        }
    }

    public double getZoom() {
        return this.zoom;
    }

    @Override
    public boolean close(Screen to) {
        return !this.ended;
    }
}
