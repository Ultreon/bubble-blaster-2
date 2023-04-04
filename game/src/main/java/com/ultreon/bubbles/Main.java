package com.ultreon.bubbles;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.preloader.PreClassLoader;
import com.ultreon.preloader.PreGameLoader;
import net.fabricmc.loader.impl.util.Arguments;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * The actual main class.
 * Btw, not the main startup-class that's from the Manifest: {@linkplain PreGameLoader}
 *
 * @see PreGameLoader
 */
public class Main {
    public static PreClassLoader mainClassLoader;
    public static boolean rpcReady;

    // Main (static) method. Game starts from here.
    @SuppressWarnings("unused")
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.z
        SwingUtilities.invokeAndWait(() -> {
            try {
                Arguments arguments = new Arguments();
                arguments.parse(args);
                BubbleBlaster.launch(arguments);
            } catch (Exception e) {
                System.err.println("Cannot invoke launch method.");
                System.err.println("Cannot load Game class: " + BubbleBlaster.class);
                e.printStackTrace();
                System.exit(1);
            } catch (Error e) {
                System.err.println("Cannot invoke launch method. (Internal error occurred)");
                System.err.println("Cannot load Game class: " + BubbleBlaster.class);
                e.printStackTrace();
                System.exit(1);
            } catch (Throwable t) {
                System.err.println("Cannot invoke launch method. (Internal hard error occurred)");
                System.err.println("Cannot load Game class: " + BubbleBlaster.class);
                t.printStackTrace();
                System.exit(1);
            }
        });
    }
}
