package com.ultreon.bubbles;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ultreon.libs.crash.v0.CrashLog;
import net.fabricmc.loader.impl.util.Arguments;

/**
 * Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
 */
public class DesktopLauncher {
    public static void main(String[] argv) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setBackBufferConfig(8, 8, 8, 8, 32, 0, 16);
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setIdleFPS(5);
        config.setInitialVisible(false);
        config.setWindowedMode(Constants.DEFAULT_SIZE.x, Constants.DEFAULT_SIZE.y);
        config.setTitle("Bubble Blaster 2");
        config.setWindowIcon("assets/bubbleblaster/icon.png");

        Arguments arguments = new Arguments();
        arguments.parse(argv);

        BubbleBlaster game;
        try {
            game = BubbleBlaster.launch(arguments);
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Launch failure", t).createCrash());
            Runtime.getRuntime().halt(1);
            return;
        }

        new Lwjgl3Application(game, config);
    }
}
