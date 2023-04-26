package com.ultreon.bubbles;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.libs.crash.v0.CrashLog;
import net.fabricmc.loader.impl.util.Arguments;

/**
 * Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
 */
public class DesktopLauncher {
    public static void main(String[] argv) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setTitle("Bubble Blaster 2");
        Arguments arguments = new Arguments();
        arguments.parse(argv);
        try {
            new Lwjgl3Application(BubbleBlaster.launch(arguments), config);
        } catch (Throwable t) {
            BubbleBlaster.crash(new CrashLog("Launch failure", t).createCrash());
        }
    }
}
