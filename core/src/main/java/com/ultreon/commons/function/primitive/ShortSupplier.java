package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@Deprecated
@FunctionalInterface
public interface ShortSupplier extends Supplier<Short> {
    @Deprecated
    @Override
    default Short get() {
        return this.getShort();
    }

    short getShort();

    default ShortSupplier and(ShortSupplier supplier) {
        return () -> (short) (this.getShort() & supplier.getShort());
    }

    default ShortSupplier or(ShortSupplier supplier) {
        return () -> (short) (this.getShort() | supplier.getShort());
    }

    default ShortSupplier add(ShortSupplier supplier) {
        return () -> (short) (this.getShort() + supplier.getShort());
    }

    default ShortSupplier sub(ShortSupplier supplier) {
        return () -> (short) (this.getShort() - supplier.getShort());
    }

    default ShortSupplier mul(ShortSupplier supplier) {
        return () -> (short) (this.getShort() * supplier.getShort());
    }

    default ShortSupplier div(ShortSupplier supplier) {
        return () -> (short) (this.getShort() / supplier.getShort());
    }

    default ShortSupplier mod(ShortSupplier supplier) {
        return () -> (short) (this.getShort() % supplier.getShort());
    }

    default ShortSupplier pow(ShortSupplier supplier) {
        return () -> (short) Math.pow(this.getShort(), supplier.getShort());
    }

    default ShortSupplier sqrt() {
        return () -> (short) Math.sqrt(this.getShort());
    }

    default ShortSupplier round() {
        return () -> (short) Math.round(this.getShort());
    }

    default ShortSupplier sin() {
        return () -> (short) Math.sin(this.getShort());
    }

    default ShortSupplier cos() {
        return () -> (short) Math.cos(this.getShort());
    }

    default ShortSupplier tan() {
        return () -> (short) Math.tan(this.getShort());
    }

    default ShortSupplier asin() {
        return () -> (short) Math.asin(this.getShort());
    }

    default ShortSupplier acos() {
        return () -> (short) Math.acos(this.getShort());
    }

    default ShortSupplier atan() {
        return () -> (short) Math.atan(this.getShort());
    }

    default ShortSupplier atan2(ShortSupplier supplier) {
        return () -> (short) Math.atan2(this.getShort(), supplier.getShort());
    }

    default ShortSupplier sinh() {
        return () -> (short) Math.sinh(this.getShort());
    }

    default ShortSupplier cosh() {
        return () -> (short) Math.cosh(this.getShort());
    }

    default ShortSupplier tanh() {
        return () -> (short) Math.tanh(this.getShort());
    }
}
