package com.ultreon.commons.utilities.python.builtins;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.math.BigInteger;

/**
 * @deprecated replaced by {@link BigInteger}.
 */
@Deprecated
public class Int extends BigInteger {
    public Int(String val, int radix) {
        super(val, radix);
    }

    public Int(@NonNull String val) {
        super(val);
    }
}
