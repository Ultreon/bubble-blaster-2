package com.ultreon.dev;

import com.google.gson.Gson;
import com.ultreon.preloader.PreGameLoader;

public class GameDevMain {
    private static DevClassPath classPath;
    private static boolean isRanFromHere = false;

    public static void main(String[] args) {
        isRanFromHere = true;

        final Gson gson = new Gson();

        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.
        String devClassPath = System.getenv("DEV_CLASS_PATH");

        if (devClassPath == null) throw new NullPointerException("DEV_CLASS_PATH environment variable is absent.");

        classPath = gson.fromJson(devClassPath, DevClassPath.class);
        classPath.string(devClassPath);

        PreGameLoader.main(args);
    }

    public static DevClassPath getClassPath() {
        return classPath;
    }

    public static boolean isRanFromHere() {
        return isRanFromHere;
    }
}
