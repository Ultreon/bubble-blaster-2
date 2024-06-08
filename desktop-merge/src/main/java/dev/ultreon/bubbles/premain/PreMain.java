package dev.ultreon.bubbles.premain;

import net.fabricmc.loader.impl.launch.knot.KnotClient;

public class PreMain {
    public static void main(String[] args) {
        try {
            KnotClient.main(args);
        } catch (Throwable e) {
            new ErrorWindow(e);
        }
    }
}
