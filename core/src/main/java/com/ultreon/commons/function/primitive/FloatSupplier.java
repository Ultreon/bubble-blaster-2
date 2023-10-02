package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@Deprecated
@FunctionalInterface
public interface FloatSupplier extends Supplier<Float> {
    @Deprecated
    @Override
    default Float get() {
        return this.getFloat();
    }

    float getFloat();

    default FloatSupplier add(FloatSupplier supplier) {
        return () -> this.getFloat() + supplier.getFloat();
    }

    default FloatSupplier sub(FloatSupplier supplier) {
        return () -> this.getFloat() - supplier.getFloat();
    }

    default FloatSupplier mul(FloatSupplier supplier) {
        return () -> this.getFloat() * supplier.getFloat();
    }

    default FloatSupplier div(FloatSupplier supplier) {
        return () -> this.getFloat() / supplier.getFloat();
    }

    default FloatSupplier mod(FloatSupplier supplier) {
        return () -> this.getFloat() % supplier.getFloat();
    }

    default FloatSupplier pow(FloatSupplier supplier) {
        return () -> (float) Math.pow(this.getFloat(), supplier.getFloat());
    }

    default FloatSupplier sqrt() {
        return () -> (float) Math.sqrt(this.getFloat());
    }

    default IntSupplier round() {
        return () -> Math.round(this.getFloat());
    }

    default FloatSupplier roundFloat() {
        return () -> (float) Math.round(this.getFloat());
    }

    default FloatSupplier sin() {
        return () -> (float) Math.sin(this.getFloat());
    }

    default FloatSupplier cos() {
        return () -> (float) Math.cos(this.getFloat());
    }

    default FloatSupplier tan() {
        return () -> (float) Math.tan(this.getFloat());
    }

    default FloatSupplier asin() {
        return () -> (float) Math.asin(this.getFloat());
    }

    default FloatSupplier acos() {
        return () -> (float) Math.acos(this.getFloat());
    }

    default FloatSupplier atan() {
        return () -> (float) Math.atan(this.getFloat());
    }

    default FloatSupplier atan2(FloatSupplier supplier) {
        return () -> (float) Math.atan2(this.getFloat(), supplier.getFloat());
    }

    default FloatSupplier sinh() {
        return () -> (float) Math.sinh(this.getFloat());
    }

    default FloatSupplier cosh() {
        return () -> (float) Math.cosh(this.getFloat());
    }

    default FloatSupplier tanh() {
        return () -> (float) Math.tanh(this.getFloat());
    }
}
