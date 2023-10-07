package com.ultreon.bubbles;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ultreon.bubbles.platform.desktop.DesktopPlatform;
import com.ultreon.libs.crash.v0.CrashLog;
import net.fabricmc.loader.impl.util.Arguments;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
 */
public class DesktopLauncher {
    private static final int[] SIZES = {
            16, 24, 32, 48, 64, 72, 96, 108, 128, 256
    };

    public static void main(String[] argv) {
        var config = DesktopLauncher.createConfig();


        var arguments = new Arguments();
        arguments.parse(argv);
        var platform = new DesktopPlatform(arguments);

        GameLibGDXWrapper game;
        try {
            game = new GameLibGDXWrapper(platform);
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Launch failure", t).createCrash());
            return;
        }

        try {
            new Lwjgl3Application(game, config);
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Game crashed :(", t).createCrash());
        }

        Runtime.getRuntime().exit(0);
    }

    @NotNull
    private static Lwjgl3ApplicationConfiguration createConfig() {
        List<String> icons = new ArrayList<>();
        for (var size : SIZES) {
            icons.add("assets/bubbleblaster/icons/icon" + size + ".png");
        }

        var config = new Lwjgl3ApplicationConfiguration();
        config.setBackBufferConfig(8, 8, 8, 8, 32, 0, 16);
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setIdleFPS(5);
        config.setInitialVisible(false);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2);
        config.setWindowedMode(Constants.DEFAULT_SIZE.x, Constants.DEFAULT_SIZE.y);
        config.setTitle("Bubble Blaster 2");
        config.setWindowIcon(icons.toArray(new String[]{}));
        return config;
    }
}
