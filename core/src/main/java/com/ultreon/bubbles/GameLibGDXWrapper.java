package com.ultreon.bubbles;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.Nullable;

public class GameLibGDXWrapper implements ApplicationListener {
    @Nullable
    private BubbleBlaster game;
    private final GamePlatform platform;

    public GameLibGDXWrapper(GamePlatform platform) {
        this.platform = platform;
    }

    @Override
    public void create() {
        Gdx.graphics.setForegroundFPS(120);
        try {
            this.game = BubbleBlaster.launch(this.platform);
        } catch (Throwable t) {
            BubbleBlaster.crash(t);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (this.game != null) this.game.resize(width, height);
    }

    @Override
    public void render() {
        if (this.game != null) this.game.render();
    }

    @Override
    public void pause() {
        if (this.game != null) this.game.pause();
    }

    @Override
    public void resume() {
        if (this.game != null) this.game.resume();
    }

    @Override
    public void dispose() {
        if (this.game != null) this.game.dispose();
    }
}
