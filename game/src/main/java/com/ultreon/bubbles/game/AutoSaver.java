package com.ultreon.bubbles.game;

import com.ultreon.commons.lang.DummyMessenger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;

class AutoSaver extends Thread implements Runnable {
    private static final Marker MARKER = MarkerFactory.getMarker("AutoSaver");
    private final LoadedGame loadedGame;

    public AutoSaver(LoadedGame loadedGame) {
        super("Auto-Saver");
        this.loadedGame = loadedGame;
    }

    @Override
    public void run() {
        long nextSave = System.currentTimeMillis();
        while (!loadedGame.getEnvironment().isGameOver()) {
            if (nextSave - System.currentTimeMillis() < 0) {
                BubbleBlaster.getLogger().info("Auto Saving...");
                onAutoSave();
                nextSave = System.currentTimeMillis() + 30000;
            }
            try {
                //noinspection BusyWait
                sleep(30000);
            } catch (InterruptedException e) {
                BubbleBlaster.getLogger().warn("Could not sleep thread.");
            }
        }
        BubbleBlaster.getLogger().debug("Stopping AutoSaveThread...");
    }

    void autoSaveThread(LoadedGame loadedGame) {
    }

    private void onAutoSave() {
        try {
            loadedGame.getEnvironment().save(loadedGame.getGameSave(), new DummyMessenger());
        } catch (IOException e) {
            BubbleBlaster.getLogger().warn(MARKER, "Auto-saving failed:", e);
        }
    }
}
