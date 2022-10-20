package com.ultreon.commons.os;

public enum OperatingSystem {
    WINDOWS("Windows"),
    UNIX("Unix"),
    LINUX("Linux"),
    OSX("OSX"),
    DARWIN("Darwin"),
    SOLARIS("Solaris"),
    ANDROID("Android"),
    IOS("iOS"),
    UNKNOWN("<<UNKNOWN>>"),
    OS_2("OS/2"),
    AIX("AIX"),
    DOS("DOS"),
    FREE_BSD("FreeBSD"),
    IRIX("Irix"),
    DIGITAL_UNIX("Digital Unix"),
    MPE_IX("MPE/iX"),
    NETWARE("Netware"),
    HP_UX("HP UX");

    private final String name;

    OperatingSystem(String name) {
        this.name = name;
    }

    public static OperatingSystem getSystem() {
        String sunDesktop = System.getProperty("os.name");
        if (sunDesktop.toLowerCase().startsWith("windows")) {
            return WINDOWS;
        } else if (sunDesktop.equalsIgnoreCase("solaris")) {
            return SOLARIS;
        } else if (sunDesktop.equalsIgnoreCase("android")) {
            return ANDROID;
        } else if (sunDesktop.equalsIgnoreCase("ios")) {
            return IOS;
        } else if (sunDesktop.equalsIgnoreCase("linux")) {
            return LINUX;
        } else if (sunDesktop.equalsIgnoreCase("darwin")) {
            return DARWIN;
        } else if (sunDesktop.equalsIgnoreCase("osx")) {
            return OSX;
        } else if (sunDesktop.equalsIgnoreCase("mac os")) {
            return OSX;
        } else if (sunDesktop.equalsIgnoreCase("mac os x")) {
            return OSX;
        } else if (sunDesktop.equalsIgnoreCase("os/2")) {
            return OS_2;
        } else if (sunDesktop.equalsIgnoreCase("aix")) {
            return AIX;
        } else if (sunDesktop.equalsIgnoreCase("freebsd")) {
            return FREE_BSD;
        } else if (sunDesktop.equalsIgnoreCase("irix")) {
            return IRIX;
        } else if (sunDesktop.equalsIgnoreCase("digital unix")) {
            return DIGITAL_UNIX;
        } else if (sunDesktop.equalsIgnoreCase("mpe/ix")) {
            return MPE_IX;
        } else if (sunDesktop.equalsIgnoreCase("hp ux")) {
            return HP_UX;
        } else if (sunDesktop.toLowerCase().startsWith("netware")) {
            return NETWARE;
        } else if (sunDesktop.equalsIgnoreCase("dos")) {
            return DOS;
        } else if (sunDesktop.equalsIgnoreCase("unix")) {
            return UNIX;
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return name;
    }
}
