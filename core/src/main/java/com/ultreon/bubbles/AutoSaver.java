package com.ultreon.bubbles;

import com.ultreon.bubbles.common.Controllable;
import com.ultreon.libs.commons.v0.DummyMessenger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

class AutoSaver implements Controllable {
    private static final Marker MARKER = MarkerFactory.getMarker("AutoSaver");
    private final LoadedGame loadedGame;
    private ScheduledFuture<?> future;
    private boolean enabled;

    public AutoSaver(LoadedGame loadedGame) {
        this.loadedGame = loadedGame;
    }

    private void run() {
        if (this.loadedGame.getWorld().isGameOver() || !this.enabled) return;

        this.onAutoSave();
    }

    private void onAutoSave() {
        var world = this.loadedGame.getWorld();
        if (world == null) {
            this.end();
            return;
        }
        try {
            this.loadedGame.getWorld().save(this.loadedGame.getGameSave(), new DummyMessenger());
        } catch (IOException e) {
            BubbleBlaster.getLogger().warn(MARKER, "Auto-saving failed:", e);
        }
    }

    @Override
    public void begin() {
        this.enabled = true;
        this.future = this.loadedGame.schedulerService.scheduleAtFixedRate(this::run, Constants.AUTO_SAVE_RATE, Constants.AUTO_SAVE_RATE, Constants.AUTO_SAVE_RATE_UNIT);
    }

    @Override
    public void end() {
        this.future.cancel(false);
        this.enabled = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
