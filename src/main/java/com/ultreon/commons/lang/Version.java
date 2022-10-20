package com.ultreon.commons.lang;

import java.io.Serializable;
import java.util.Objects;

public final class Version implements Serializable {
    private static final long serialVersionUID = 0L;
    private final int major;
    private final int minor;
    private final int build;
    private final VersionType type;
    private final int release;

    public Version(int major, int minor, int build, VersionType type,
                   int release) {
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.type = type;
        this.release = release;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + build + "-" + type.getName() + release;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major &&
                minor == version.minor &&
                release == version.release &&
                type == version.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, type, release);
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int build() {
        return build;
    }

    public VersionType type() {
        return type;
    }

    public int release() {
        return release;
    }

}
