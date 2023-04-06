package com.ultreon.commons.utilities.python.builtins;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * @deprecated replaced by {@link BigInteger}.
 */
@Deprecated
public class Int extends BigInteger {
    public Int(String val, int radix) {
        super(val, radix);
    }

    public Int(@NotNull String val) {
        super(val);
    }
}
