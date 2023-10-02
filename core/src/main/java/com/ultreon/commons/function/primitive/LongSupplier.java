package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@Deprecated
@FunctionalInterface
public interface LongSupplier extends Supplier<Long> {
    @Deprecated
    @Override
    default Long get() {
        return this.getLong();
    }

    long getLong();

    default LongSupplier and(LongSupplier supplier) {
        return () -> this.getLong() & supplier.getLong();
    }

    default LongSupplier or(LongSupplier supplier) {
        return () -> this.getLong() | supplier.getLong();
    }

    default LongSupplier add(LongSupplier supplier) {
        return () -> this.getLong() + supplier.getLong();
    }

    default LongSupplier sub(LongSupplier supplier) {
        return () -> this.getLong() - supplier.getLong();
    }

    default LongSupplier mul(LongSupplier supplier) {
        return () -> this.getLong() * supplier.getLong();
    }

    default LongSupplier div(LongSupplier supplier) {
        return () -> this.getLong() / supplier.getLong();
    }

    default LongSupplier mod(LongSupplier supplier) {
        return () -> this.getLong() % supplier.getLong();
    }

    default LongSupplier pow(LongSupplier supplier) {
        return () -> (long) Math.pow(this.getLong(), supplier.getLong());
    }

    default LongSupplier sqrt() {
        return () -> (long) Math.sqrt(this.getLong());
    }

    default LongSupplier round() {
        return () -> (long) Math.round(this.getLong());
    }

    default LongSupplier sin() {
        return () -> (long) Math.sin(this.getLong());
    }

    default LongSupplier cos() {
        return () -> (long) Math.cos(this.getLong());
    }

    default LongSupplier tan() {
        return () -> (long) Math.tan(this.getLong());
    }

    default LongSupplier asin() {
        return () -> (long) Math.asin(this.getLong());
    }

    default LongSupplier acos() {
        return () -> (long) Math.acos(this.getLong());
    }

    default LongSupplier atan() {
        return () -> (long) Math.atan(this.getLong());
    }

    default LongSupplier atan2(LongSupplier supplier) {
        return () -> (long) Math.atan2(this.getLong(), supplier.getLong());
    }

    default LongSupplier sinh() {
        return () -> (long) Math.sinh(this.getLong());
    }

    default LongSupplier cosh() {
        return () -> (long) Math.cosh(this.getLong());
    }

    default LongSupplier tanh() {
        return () -> (long) Math.tanh(this.getLong());
    }
}
