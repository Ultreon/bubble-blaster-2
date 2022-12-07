package com.ultreon.commons.function.primitive;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiByte2ByteFunction extends BiFunction<Byte, Byte, Byte> {
    @Override
    @Deprecated
    default Byte apply(Byte a, Byte b) {
        return apply((byte) a, (byte) b);
    }

    byte apply(byte a, byte b);

    static BiByte2ByteFunction and() {
        return (x, y) -> (byte) (x & y);
    }

    static BiByte2ByteFunction or() {
        return (x, y) -> (byte) (x | y);
    }

    static BiByte2ByteFunction add() {
        return (x, y) -> (byte) (x + y);
    }

    static BiByte2ByteFunction sub() {
        return (x, y) -> (byte) (x - y);
    }

    static BiByte2ByteFunction mul() {
        return (x, y) -> (byte) (x * y);
    }

    static BiByte2ByteFunction div() {
        return (x, y) -> (byte) (x / y);
    }

    static BiByte2ByteFunction mod() {
        return (x, y) -> (byte) (x % y);
    }

    static BiByte2ByteFunction pow() {
        return (x, y) -> (byte) Math.pow(x, y);
    }

    static BiByte2ByteFunction atan2() {
        return (x, y) -> (byte) Math.atan2(x, y);
    }

    static BiByte2ByteFunction scalb() {
        return (x, y) -> (byte) Math.scalb(x, y);
    }
}
