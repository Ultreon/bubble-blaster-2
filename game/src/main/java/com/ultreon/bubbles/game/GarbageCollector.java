package com.ultreon.bubbles.game;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class GarbageCollector extends Thread {
    private final BubbleBlaster game;
    private final Marker marker = MarkerFactory.getMarker("GC");

    public GarbageCollector(BubbleBlaster game) {
        super("Garbage Collector");
        this.game = game;
    }

    @Override
    public void run() {
        try {
            while (game.isRunning()) {
                System.gc();
                Thread.sleep(50);
            }
        } catch (InterruptedException ignored) {
            BubbleBlaster.getLogger().warn(marker, "Unexpected interruption in garbage collector");
        }
        BubbleBlaster.getLogger().info("Shutting down garbage collector.");
    }
}
