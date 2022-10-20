package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@FunctionalInterface
public interface IntSupplier extends Supplier<Integer> {
    @Deprecated
    @Override
    default Integer get() {
        return getInt();
    }

    int getInt();

    default IntSupplier and(IntSupplier supplier) {
        return () -> getInt() & supplier.getInt();
    }

    default IntSupplier or(IntSupplier supplier) {
        return () -> getInt() | supplier.getInt();
    }

    default IntSupplier add(IntSupplier supplier) {
        return () -> getInt() + supplier.getInt();
    }

    default IntSupplier sub(IntSupplier supplier) {
        return () -> getInt() - supplier.getInt();
    }

    default IntSupplier mul(IntSupplier supplier) {
        return () -> getInt() * supplier.getInt();
    }

    default IntSupplier div(IntSupplier supplier) {
        return () -> getInt() / supplier.getInt();
    }

    default IntSupplier mod(IntSupplier supplier) {
        return () -> getInt() % supplier.getInt();
    }

    default IntSupplier pow(IntSupplier supplier) {
        return () -> (int) Math.pow(getInt(), supplier.getInt());
    }

    default IntSupplier sqrt() {
        return () -> (int) Math.sqrt(getInt());
    }

    default IntSupplier round() {
        return () -> Math.round(getInt());
    }

    default IntSupplier sin() {
        return () -> (int) Math.sin(getInt());
    }

    default IntSupplier cos() {
        return () -> (int) Math.cos(getInt());
    }

    default IntSupplier tan() {
        return () -> (int) Math.tan(getInt());
    }

    default IntSupplier asin() {
        return () -> (int) Math.asin(getInt());
    }

    default IntSupplier acos() {
        return () -> (int) Math.acos(getInt());
    }

    default IntSupplier atan() {
        return () -> (int) Math.atan(getInt());
    }

    default IntSupplier atan2(IntSupplier supplier) {
        return () -> (int) Math.atan2(getInt(), supplier.getInt());
    }

    default IntSupplier sinh() {
        return () -> (int) Math.sinh(getInt());
    }

    default IntSupplier cosh() {
        return () -> (int) Math.cosh(getInt());
    }

    default IntSupplier tanh() {
        return () -> (int) Math.tanh(getInt());
    }
}
