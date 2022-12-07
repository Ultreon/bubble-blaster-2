package com.ultreon.commons.utilities.python.builtins;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @deprecated replaced by {@link BigDecimal}
 */
@Deprecated
public class Float extends BigDecimal {
    public Float(String val) {
        super(val);
    }

    public Float(String val, MathContext mc) {
        super(val, mc);
    }

    public Float(double val) {
        super(val);
    }

    public Float(double val, MathContext mc) {
        super(val, mc);
    }

    public Float(int val) {
        super(val);
    }

    public Float(int val, MathContext mc) {
        super(val, mc);
    }

    public Float(long val) {
        super(val);
    }

    public Float(long val, MathContext mc) {
        super(val, mc);
    }
}
