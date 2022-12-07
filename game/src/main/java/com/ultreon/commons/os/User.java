package com.ultreon.commons.os;

import java.io.Serializable;

@SuppressWarnings("unused")
public class User implements Serializable {
    private final String name;
    private final String variant;
    private final String home;
    private final String dir;
    private final String countryName;
    private final String language;

    @Deprecated
    public User() {
        this(
                System.getProperty("user.name"), System.getProperty("user.variant"),    // Name & variant
                System.getProperty("user.home"), System.getProperty("user.dir"),        // Home & directory
                System.getProperty("user.country"), System.getProperty("user.language") // Country & language
        );
    }

    public static User getCurrentUser() {
        return new User(
                System.getProperty("user.name"), System.getProperty("user.variant"),    // Name & variant
                System.getProperty("user.home"), System.getProperty("user.dir"),        // Home & directory
                System.getProperty("user.country"), System.getProperty("user.language") // Country & language
        );
    }

    public User(String name) {
        this(name, null, null, null, null, null);
    }

    public User(String name, String variant, String home, String dir, String countryName, String language) {
        this.name = name;
        this.variant = variant;
        this.home = home;
        this.dir = dir;
        this.countryName = countryName;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getVariant() {
        return variant;
    }

    public String getHome() {
        return home;
    }

    public String getDir() {
        return dir;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getLanguage() {
        return language;
    }
}
