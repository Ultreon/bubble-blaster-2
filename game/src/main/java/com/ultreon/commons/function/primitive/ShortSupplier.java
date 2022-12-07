package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@FunctionalInterface
public interface ShortSupplier extends Supplier<Short> {
    @Deprecated
    @Override
    default Short get() {
        return getShort();
    }

    short getShort();

    default ShortSupplier and(ShortSupplier supplier) {
        return () -> (short) (getShort() & supplier.getShort());
    }

    default ShortSupplier or(ShortSupplier supplier) {
        return () -> (short) (getShort() | supplier.getShort());
    }

    default ShortSupplier add(ShortSupplier supplier) {
        return () -> (short) (getShort() + supplier.getShort());
    }

    default ShortSupplier sub(ShortSupplier supplier) {
        return () -> (short) (getShort() - supplier.getShort());
    }

    default ShortSupplier mul(ShortSupplier supplier) {
        return () -> (short) (getShort() * supplier.getShort());
    }

    default ShortSupplier div(ShortSupplier supplier) {
        return () -> (short) (getShort() / supplier.getShort());
    }

    default ShortSupplier mod(ShortSupplier supplier) {
        return () -> (short) (getShort() % supplier.getShort());
    }

    default ShortSupplier pow(ShortSupplier supplier) {
        return () -> (short) Math.pow(getShort(), supplier.getShort());
    }

    default ShortSupplier sqrt() {
        return () -> (short) Math.sqrt(getShort());
    }

    default ShortSupplier round() {
        return () -> (short) Math.round(getShort());
    }

    default ShortSupplier sin() {
        return () -> (short) Math.sin(getShort());
    }

    default ShortSupplier cos() {
        return () -> (short) Math.cos(getShort());
    }

    default ShortSupplier tan() {
        return () -> (short) Math.tan(getShort());
    }

    default ShortSupplier asin() {
        return () -> (short) Math.asin(getShort());
    }

    default ShortSupplier acos() {
        return () -> (short) Math.acos(getShort());
    }

    default ShortSupplier atan() {
        return () -> (short) Math.atan(getShort());
    }

    default ShortSupplier atan2(ShortSupplier supplier) {
        return () -> (short) Math.atan2(getShort(), supplier.getShort());
    }

    default ShortSupplier sinh() {
        return () -> (short) Math.sinh(getShort());
    }

    default ShortSupplier cosh() {
        return () -> (short) Math.cosh(getShort());
    }

    default ShortSupplier tanh() {
        return () -> (short) Math.tanh(getShort());
    }
}
