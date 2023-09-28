//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ultreon.gameprovider.bubbles;

public class OS {
    private static OSType osType;

    public OS() {
    }

    public static final boolean isWindows() {
        return getOSType() == OS.OSType.OSWindows;
    }

    public static final boolean isMacintosh() {
        return getOSType() == OS.OSType.OSMacintosh;
    }

    public static final boolean isLinux() {
        return getOSType() == OS.OSType.OSLinux;
    }

    private static final OSType getOSType() {
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

    private static enum OSType {
        OSUndefined,
        OSLinux,
        OSWindows,
        OSMacintosh,
        OSUnknown;

        private OSType() {
        }
    }
}
