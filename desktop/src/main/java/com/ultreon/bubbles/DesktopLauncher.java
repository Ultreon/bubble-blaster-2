package com.ultreon.bubbles;

import com.badlogic.gdx.Graphics;
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
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setInitialVisible(false);
        config.setWindowedMode(1280, 720);
        config.setTitle("Bubble Blaster 2");
        config.setWindowIcon("assets/bubbles/icon.png");
        Arguments arguments = new Arguments();
        arguments.parse(argv);
        try {
            new Lwjgl3Application(BubbleBlaster.launch(arguments), config);
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Launch failure", t).createCrash());
        }
    }
}
