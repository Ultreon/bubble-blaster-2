package com.ultreon.bubbles;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GarbageCollector {
    private static final Marker MARKER = MarkerFactory.getMarker("GC");
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(3, r -> {
        Thread thread = new Thread(r);
        thread.setPriority(1);
        return thread;
    });

    public GarbageCollector() {
        service.scheduleAtFixedRate(System::gc, 10, 5, TimeUnit.SECONDS);
    }

    public void shutdown() {
        service.shutdownNow();

        BubbleBlaster.getLogger().info(MARKER, "Shutting down garbage collector.");
    }
}
