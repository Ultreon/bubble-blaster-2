package com.ultreon.bubbles.util;

import java.util.UUID;

public class NbtUtils {
    public static long[] saveUuid(UUID uuid) {
        return new long[]{uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()};
    }

    public static UUID loadUuid(long[] uuid) {
        return new UUID(uuid[0], uuid[1]);
    }
}
