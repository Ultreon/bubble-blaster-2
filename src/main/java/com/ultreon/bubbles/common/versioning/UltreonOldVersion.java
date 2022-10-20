package com.ultreon.bubbles.common.versioning;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
@SuppressWarnings("unused")
public class UltreonOldVersion extends AbstractVersion<UltreonOldVersion> {
    private final int version;
    private final int subversion;
    private final Type type;
    private final int release;
    public static final UltreonOldVersion EMPTY = new UltreonOldVersion(0, 0, Type.BETA, 0);

    /**
     * @param s the version to parse.
     * @throws IllegalArgumentException when an invalid version has given.
     */
    public UltreonOldVersion(String s) {
        // String to be scanned to find the pattern.
        String pattern = "([0-9]*)\\.([0-9]*)-(alpha|beta|pre|release)([0-9]*)"; // 1.0-alpha4 // 5.4-release-7

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(s);
        if (m.find()) {
            version = Integer.parseInt(m.group(1));
            subversion = Integer.parseInt(m.group(2));

            switch (m.group(3)) {
                case "alpha" -> type = Type.ALPHA;
                case "beta" -> type = Type.BETA;
                case "pre" -> type = Type.PRE;
                case "release" -> type = Type.RELEASE;
                default -> throw new InternalError("Regex has invalid output.");
            }

            release = Integer.parseInt(m.group(4));
        } else {
            throw new IllegalArgumentException("Invalid version,");
        }
    }

    public UltreonOldVersion(int version, int subversion, Type type, int release) {
        this.version = version;
        this.subversion = subversion;
        this.type = type;
        this.release = release;
    }

    public int getVersion() {
        return version;
    }

    public Type getType() {
        return type;
    }

    public int getSubversion() {
        return subversion;
    }

    public int getRelease() {
        return release;
    }

    @Override
    public boolean isStable() {
        return type == Type.RELEASE;
    }

    public String toString() {
        return String.format("%d.%d-%s%d", version, subversion, type.name().toLowerCase(), release);
    }

    @Override
    public int compareTo(@NonNull UltreonOldVersion o) {
        int cmp = Integer.compare(this.version, o.version);
        if (cmp == 0) {
            int cmp1 = Integer.compare(this.subversion, o.subversion);
            if (cmp1 == 0) {
                int cmp2 = this.type.compareTo(o.type);
                if (cmp2 == 0) {
                    return Integer.compare(this.release, o.release);
                } else {
                    return cmp2;
                }
            } else {
                return cmp1;
            }
        } else {
            return cmp;
        }
    }

    public enum Type {
        ALPHA,
        BETA,
        PRE,
        RELEASE
    }
}
