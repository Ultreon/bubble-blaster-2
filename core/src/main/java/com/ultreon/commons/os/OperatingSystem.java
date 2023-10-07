package com.ultreon.commons.os;

public enum OperatingSystem {
    Windows("Windows"),
    Unix("Unix"),
    Linux("Linux"),
    OsX("OSX"),
    MacOS("macOS"),
    Darwin("Darwin"),
    Solaris("Solaris"),
    Android("Android"),
    IOS("iOS"),
    OS2("OS/2"),
    Aix("AIX"),
    Dos("DOS"),
    FreeBSD("FreeBSD"),
    Irix("Irix"),
    DigitalUnix("Digital Unix"),
    MPE_iX("MPE/iX"),
    Netware("Netware"),
    HP_UX("HP UX"),
    Unknown("<<UNKNOWN>>");

    private final String name;

    OperatingSystem(String name) {
        this.name = name;
    }

    public static OperatingSystem getSystem() {
        var osName = System.getProperty("os.name");

        if (osName.toLowerCase().startsWith("windows")) return Windows;
        else if (osName.equalsIgnoreCase("solaris")) return Solaris;
        else if (osName.equalsIgnoreCase("android")) return Android;
        else if (osName.equalsIgnoreCase("ios")) return IOS;
        else if (osName.equalsIgnoreCase("linux")) return Linux;
        else if (osName.equalsIgnoreCase("darwin")) return Darwin;
        else if (osName.equalsIgnoreCase("osx")) return OsX;
        else if (osName.equalsIgnoreCase("macos")) return MacOS;
        else if (osName.equalsIgnoreCase("mac os")) return MacOS;
        else if (osName.equalsIgnoreCase("macos x")) return MacOS;
        else if (osName.equalsIgnoreCase("mac os x")) return MacOS;
        else if (osName.equalsIgnoreCase("os/2")) return OS2;
        else if (osName.equalsIgnoreCase("aix")) return Aix;
        else if (osName.equalsIgnoreCase("freebsd")) return FreeBSD;
        else if (osName.equalsIgnoreCase("irix")) return Irix;
        else if (osName.equalsIgnoreCase("digital unix")) return DigitalUnix;
        else if (osName.equalsIgnoreCase("mpe/ix")) return MPE_iX;
        else if (osName.equalsIgnoreCase("hp ux")) return HP_UX;
        else if (osName.toLowerCase().startsWith("netware")) return Netware;
        else if (osName.equalsIgnoreCase("dos")) return Dos;
        else if (osName.equalsIgnoreCase("unix")) return Unix;

        return Unknown;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
