package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@FunctionalInterface
public interface FloatSupplier extends Supplier<Float> {
    @Deprecated
    @Override
    default Float get() {
        return getFloat();
    }

    float getFloat();

    default FloatSupplier add(FloatSupplier supplier) {
        return () -> getFloat() + supplier.getFloat();
    }

    default FloatSupplier sub(FloatSupplier supplier) {
        return () -> getFloat() - supplier.getFloat();
    }

    default FloatSupplier mul(FloatSupplier supplier) {
        return () -> getFloat() * supplier.getFloat();
    }

    default FloatSupplier div(FloatSupplier supplier) {
        return () -> getFloat() / supplier.getFloat();
    }

    default FloatSupplier mod(FloatSupplier supplier) {
        return () -> getFloat() % supplier.getFloat();
    }

    default FloatSupplier pow(FloatSupplier supplier) {
        return () -> (float) Math.pow(getFloat(), supplier.getFloat());
    }

    default FloatSupplier sqrt() {
        return () -> (float) Math.sqrt(getFloat());
    }

    default IntSupplier round() {
        return () -> Math.round(getFloat());
    }

    default FloatSupplier roundFloat() {
        return () -> (float) Math.round(getFloat());
    }

    default FloatSupplier sin() {
        return () -> (float) Math.sin(getFloat());
    }

    default FloatSupplier cos() {
        return () -> (float) Math.cos(getFloat());
    }

    default FloatSupplier tan() {
        return () -> (float) Math.tan(getFloat());
    }

    default FloatSupplier asin() {
        return () -> (float) Math.asin(getFloat());
    }

    default FloatSupplier acos() {
        return () -> (float) Math.acos(getFloat());
    }

    default FloatSupplier atan() {
        return () -> (float) Math.atan(getFloat());
    }

    default FloatSupplier atan2(FloatSupplier supplier) {
        return () -> (float) Math.atan2(getFloat(), supplier.getFloat());
    }

    default FloatSupplier sinh() {
        return () -> (float) Math.sinh(getFloat());
    }

    default FloatSupplier cosh() {
        return () -> (float) Math.cosh(getFloat());
    }

    default FloatSupplier tanh() {
        return () -> (float) Math.tanh(getFloat());
    }
}
