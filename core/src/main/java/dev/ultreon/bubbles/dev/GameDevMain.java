package dev.ultreon.bubbles.dev;

import dev.ultreon.bubbles.GamePlatform;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("ALL")
public class GameDevMain {
    private static final Logger LOGGER = GamePlatform.get().getLogger("GameDevMain");
    private static DevClassPath classPath;
    private static boolean isRanFromHere = false;

    public static void main(String[] args) {
        isRanFromHere = true;
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.

        final String keyStart = "dev.ultreon.dev.sources.";
        classPath = new DevClassPath();
        Properties properties = System.getProperties();
        properties.keySet().forEach(obj -> {
            String key = obj.toString();
            if (key.startsWith(keyStart)) {
                String name = key.substring(keyStart.length());
                Object o = properties.get(obj);
                String classPath = o.toString();
                System.out.println("Classpath (" + name + ") -> " + classPath);
                String[] split = classPath.split(System.getProperty("path.separator"));
                GameDevMain.classPath.computeIfAbsent(name, s -> new ArrayList<>()).addAll(List.of(split));
            }
        });
    }

    public static DevClassPath getClassPath() {
        return classPath;
    }

    public static boolean isRanFromHere() {
        return isRanFromHere;
    }
}
