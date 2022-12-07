package com.ultreon.preloader;

import com.ultreon.preloader.bubbleblaster.BubbleBlasterLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * Pre game loader, currently only for QBubbles.
 *
 * @since 0.0.0
 */
@SuppressWarnings({"deprecation"})
public class PreGameLoader {
    static {
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.
    }

    public static final Logger LOGGER = LogManager.getLogger("Pre-Loader");
    private static final String BUBBLE_BLASTER_LOADER = "com.ultreon.preloader.bubbleblaster.BubbleBlasterLoader";

    public static void main(String[] args) {
        new PreGameLoader().loadGame(args);
    }

    public static PreClassLoader classLoader;

    private PreGameLoader() {
        URL[] urls = new URL[0];
        try {
            urls = new URL[]{PreGameLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL()};
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }

        classLoader = new PreClassLoader(urls);
    }

    public static URL getPreMainUrl() {
        return PreGameLoader.class.getProtectionDomain().getClassLoader()
                .getClass().getProtectionDomain().getCodeSource().getLocation();
    }

    private void loadGame(String[] args) {
        try {
            BubbleBlasterLoader loader;

            String loaderLocation = BUBBLE_BLASTER_LOADER;
            LOGGER.info(String.format("Using loader from location: %s", loaderLocation));

            // Add the internal package and get the loader class from the loader location..
            classLoader.addInternalPackage(loaderLocation.substring(0, loaderLocation.lastIndexOf('.')));
            loader = (BubbleBlasterLoader) Class.forName(loaderLocation, true, classLoader).newInstance();

            // Set arguments of loader.
            LOGGER.info(String.format("Current loader class %s", loader.getClass().getName()));

            final String loadTarget = Objects.requireNonNull(loader).getLoadingTarget();
            final Class<?> clazz = Class.forName(loadTarget, false, getClass().getClassLoader());
            final Method mainMethod = clazz.getMethod("main", String[].class, PreClassLoader.class);

            LOGGER.info(String.format("Loading Bubble Blaster {%s}", loadTarget));
            mainMethod.invoke(null, args, classLoader);
        } catch (Exception e) {
            LOGGER.fatal("Problem occurred when trying to load a game.", e);
            System.exit(1);
        }
    }
}
