package com.ultreon.commons.lang;

import java.io.Serializable;
import java.util.Objects;

@Deprecated
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
        return this.major + "." + this.minor + "." + this.build + "-" + this.type.getName() + this.release;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return this.major == version.major &&
                this.minor == version.minor &&
                this.release == version.release &&
                this.type == version.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.major, this.minor, this.type, this.release);
    }

    public int major() {
        return this.major;
    }

    public int minor() {
        return this.minor;
    }

    public int build() {
        return this.build;
    }

    public VersionType type() {
        return this.type;
    }

    public int release() {
        return this.release;
    }

}
