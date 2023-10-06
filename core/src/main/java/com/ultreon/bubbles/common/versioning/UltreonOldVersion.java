package com.ultreon.bubbles.common.versioning;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
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
            this.version = Integer.parseInt(m.group(1));
            this.subversion = Integer.parseInt(m.group(2));

            switch (m.group(3)) {
                case "alpha":
                    this.type = Type.ALPHA;
                    break;
                case "beta":
                    this.type = Type.BETA;
                    break;
                case "pre":
                    this.type = Type.PRE;
                    break;
                case "release":
                    this.type = Type.RELEASE;
                    break;
                default:
                    throw new InternalError("Regex has invalid output.");
            }

            this.release = Integer.parseInt(m.group(4));
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
        return this.version;
    }

    public Type getType() {
        return this.type;
    }

    public int getSubversion() {
        return this.subversion;
    }

    public int getRelease() {
        return this.release;
    }

    @Override
    public boolean isStable() {
        return this.type == Type.RELEASE;
    }

    public String toString() {
        return String.format("%d.%d-%s%d", this.version, this.subversion, this.type.name().toLowerCase(), this.release);
    }

    @Override
    public int compareTo(@NotNull UltreonOldVersion o) {
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
