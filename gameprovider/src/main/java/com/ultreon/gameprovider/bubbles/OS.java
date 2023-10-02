//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ultreon.gameprovider.bubbles;

public class OS {
    private static OSType osType;

    public OS() {
    }

    public static boolean isWindows() {
        return OS.getOSType() == OS.OSType.OSWindows;
    }

    public static boolean isMacintosh() {
        return OS.getOSType() == OS.OSType.OSMacintosh;
    }

    public static boolean isLinux() {
        return OS.getOSType() == OS.OSType.OSLinux;
    }

    private static OSType getOSType() {
        if (osType == OS.OSType.OSUndefined) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows")) {
                osType = OS.OSType.OSWindows;
            } else if (os.startsWith("linux")) {
                osType = OS.OSType.OSLinux;
            } else if (os.startsWith("mac")) {
                osType = OS.OSType.OSMacintosh;
            } else {
                osType = OS.OSType.OSUnknown;
            }
        }

        return osType;
    }

    static {
        osType = OS.OSType.OSUndefined;
    }

    private enum OSType {
        OSUndefined,
        OSLinux,
        OSWindows,
        OSMacintosh,
        OSUnknown;

        OSType() {
        }
    }
}
