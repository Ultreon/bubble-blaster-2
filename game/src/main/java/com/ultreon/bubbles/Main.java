package com.ultreon.bubbles;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.libs.crash.v0.CrashLog;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.util.Arguments;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static boolean rpcReady;

    // Main (static) method. Game starts from here.
    @SuppressWarnings("unused")
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.z

        AtomicReference<Throwable> error = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                Arguments arguments = new Arguments();
                arguments.parse(args);
                BubbleBlaster.launch(arguments);
            } catch (Exception e) {
                System.err.println("Cannot invoke launch method.");
                error.set(e);
            } catch (Error e) {
                System.err.println("Cannot invoke launch method. (Internal error occurred)");
                error.set(e);
            } catch (Throwable t) {
                System.err.println("Cannot invoke launch method. (Internal hard error occurred)");
                error.set(t);
            }
        });

        Throwable ex = error.get();
        if (ex != null) {
            try {
                BubbleBlaster.crash(new CrashLog("Failed to startup the game.", ex).createCrash());
                FabricGuiEntry.displayError("Bubble Blaster failed to start", ex, true);
            } catch (Throwable t) {
                BubbleBlaster.getLogger().error("ERROR:", t);
            }
        }
    }
}
