package com.ultreon.bubbles;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ultreon.libs.crash.v0.CrashLog;
import net.fabricmc.loader.impl.util.Arguments;
import org.jetbrains.annotations.NotNull;

/**
 * Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
 */
public class DesktopLauncher {
    public static void main(String[] argv) {
        var config = DesktopLauncher.createConfig();

        var arguments = new Arguments();
        arguments.parse(argv);

        BubbleBlaster game;
        try {
            game = BubbleBlaster.launch(arguments);
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Launch failure", t).createCrash());
            Runtime.getRuntime().halt(1);
            return;
        }

        try {
            new Lwjgl3Application(game, config);
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Game crashed :(", t).createCrash());
            Runtime.getRuntime().halt(1);
        }
    }

    @NotNull
    private static Lwjgl3ApplicationConfiguration createConfig() {
        var config = new Lwjgl3ApplicationConfiguration();
        config.setBackBufferConfig(8, 8, 8, 8, 32, 0, 16);
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setIdleFPS(5);
        config.setInitialVisible(false);
        config.setWindowedMode(Constants.DEFAULT_SIZE.x, Constants.DEFAULT_SIZE.y);
        config.setTitle("Bubble Blaster 2");
        config.setWindowIcon("assets/bubbleblaster/icon.png");
        return config;
    }
}
