package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@FunctionalInterface
public interface LongSupplier extends Supplier<Long> {
    @Deprecated
    @Override
    default Long get() {
        return getLong();
    }

    long getLong();

    default LongSupplier and(LongSupplier supplier) {
        return () -> getLong() & supplier.getLong();
    }

    default LongSupplier or(LongSupplier supplier) {
        return () -> getLong() | supplier.getLong();
    }

    default LongSupplier add(LongSupplier supplier) {
        return () -> getLong() + supplier.getLong();
    }

    default LongSupplier sub(LongSupplier supplier) {
        return () -> getLong() - supplier.getLong();
    }

    default LongSupplier mul(LongSupplier supplier) {
        return () -> getLong() * supplier.getLong();
    }

    default LongSupplier div(LongSupplier supplier) {
        return () -> getLong() / supplier.getLong();
    }

    default LongSupplier mod(LongSupplier supplier) {
        return () -> getLong() % supplier.getLong();
    }

    default LongSupplier pow(LongSupplier supplier) {
        return () -> (long) Math.pow(getLong(), supplier.getLong());
    }

    default LongSupplier sqrt() {
        return () -> (long) Math.sqrt(getLong());
    }

    default LongSupplier round() {
        return () -> (long) Math.round(getLong());
    }

    default LongSupplier sin() {
        return () -> (long) Math.sin(getLong());
    }

    default LongSupplier cos() {
        return () -> (long) Math.cos(getLong());
    }

    default LongSupplier tan() {
        return () -> (long) Math.tan(getLong());
    }

    default LongSupplier asin() {
        return () -> (long) Math.asin(getLong());
    }

    default LongSupplier acos() {
        return () -> (long) Math.acos(getLong());
    }

    default LongSupplier atan() {
        return () -> (long) Math.atan(getLong());
    }

    default LongSupplier atan2(LongSupplier supplier) {
        return () -> (long) Math.atan2(getLong(), supplier.getLong());
    }

    default LongSupplier sinh() {
        return () -> (long) Math.sinh(getLong());
    }

    default LongSupplier cosh() {
        return () -> (long) Math.cosh(getLong());
    }

    default LongSupplier tanh() {
        return () -> (long) Math.tanh(getLong());
    }
}
