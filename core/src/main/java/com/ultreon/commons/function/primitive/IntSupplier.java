package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@Deprecated
@FunctionalInterface
public interface IntSupplier extends Supplier<Integer> {
    @Deprecated
    @Override
    default Integer get() {
        return this.getInt();
    }

    int getInt();

    default IntSupplier and(IntSupplier supplier) {
        return () -> this.getInt() & supplier.getInt();
    }

    default IntSupplier or(IntSupplier supplier) {
        return () -> this.getInt() | supplier.getInt();
    }

    default IntSupplier add(IntSupplier supplier) {
        return () -> this.getInt() + supplier.getInt();
    }

    default IntSupplier sub(IntSupplier supplier) {
        return () -> this.getInt() - supplier.getInt();
    }

    default IntSupplier mul(IntSupplier supplier) {
        return () -> this.getInt() * supplier.getInt();
    }

    default IntSupplier div(IntSupplier supplier) {
        return () -> this.getInt() / supplier.getInt();
    }

    default IntSupplier mod(IntSupplier supplier) {
        return () -> this.getInt() % supplier.getInt();
    }

    default IntSupplier pow(IntSupplier supplier) {
        return () -> (int) Math.pow(this.getInt(), supplier.getInt());
    }

    default IntSupplier sqrt() {
        return () -> (int) Math.sqrt(this.getInt());
    }

    default IntSupplier round() {
        return () -> Math.round(this.getInt());
    }

    default IntSupplier sin() {
        return () -> (int) Math.sin(this.getInt());
    }

    default IntSupplier cos() {
        return () -> (int) Math.cos(this.getInt());
    }

    default IntSupplier tan() {
        return () -> (int) Math.tan(this.getInt());
    }

    default IntSupplier asin() {
        return () -> (int) Math.asin(this.getInt());
    }

    default IntSupplier acos() {
        return () -> (int) Math.acos(this.getInt());
    }

    default IntSupplier atan() {
        return () -> (int) Math.atan(this.getInt());
    }

    default IntSupplier atan2(IntSupplier supplier) {
        return () -> (int) Math.atan2(this.getInt(), supplier.getInt());
    }

    default IntSupplier sinh() {
        return () -> (int) Math.sinh(this.getInt());
    }

    default IntSupplier cosh() {
        return () -> (int) Math.cosh(this.getInt());
    }

    default IntSupplier tanh() {
        return () -> (int) Math.tanh(this.getInt());
    }
}
