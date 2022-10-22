package com.ultreon.dev;

import com.ultreon.preloader.PreGameLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("ALL")
public class GameDevMain {
    private static final Logger LOGGER = LoggerFactory.getLogger("GameDevMain");
    private static DevClassPath classPath;
    private static boolean isRanFromHere = false;

    public static void main(String[] args) {
        isRanFromHere = true;
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.

        final String keyStart = "com.ultreon.dev.sources.";
        classPath = new DevClassPath();
        Properties properties = System.getProperties();
        properties.keySet().forEach(obj -> {
            String key = obj.toString();
            if (key.startsWith(keyStart)) {
                String name = key.substring(keyStart.length());
                Object o = properties.get(obj);
                String classPath = o.toString();
                LOGGER.info("Classpath (" + name + ") -> " + classPath);
                String[] split = classPath.split(System.getProperty("path.separator"));
                GameDevMain.classPath.computeIfAbsent(name, s -> new ArrayList<>()).addAll(List.of(split));
            }
        });

        PreGameLoader.main(args);
    }

    public static DevClassPath getClassPath() {
        return classPath;
    }

    public static boolean isRanFromHere() {
        return isRanFromHere;
    }
}
