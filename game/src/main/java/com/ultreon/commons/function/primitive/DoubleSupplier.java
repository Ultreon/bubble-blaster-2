package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@FunctionalInterface
public interface DoubleSupplier extends Supplier<Double> {
    @Deprecated
    @Override
    default Double get() {
        return getDouble();
    }

    double getDouble();

    default DoubleSupplier add(DoubleSupplier supplier) {
        return () -> getDouble() + supplier.getDouble();
    }

    default DoubleSupplier sub(DoubleSupplier supplier) {
        return () -> getDouble() - supplier.getDouble();
    }

    default DoubleSupplier mul(DoubleSupplier supplier) {
        return () -> getDouble() * supplier.getDouble();
    }

    default DoubleSupplier div(DoubleSupplier supplier) {
        return () -> getDouble() / supplier.getDouble();
    }

    default DoubleSupplier mod(DoubleSupplier supplier) {
        return () -> getDouble() % supplier.getDouble();
    }

    default DoubleSupplier pow(DoubleSupplier supplier) {
        return () -> Math.pow(getDouble(), supplier.getDouble());
    }

    default DoubleSupplier sqrt() {
        return () -> Math.sqrt(getDouble());
    }

    default LongSupplier round() {
        return () -> Math.round(getDouble());
    }

    default DoubleSupplier roundDouble() {
        return () -> (double) Math.round(getDouble());
    }

    default DoubleSupplier sin() {
        return () -> Math.sin(getDouble());
    }

    default DoubleSupplier cos() {
        return () -> Math.cos(getDouble());
    }

    default DoubleSupplier tan() {
        return () -> Math.tan(getDouble());
    }

    default DoubleSupplier asin() {
        return () -> Math.asin(getDouble());
    }

    default DoubleSupplier acos() {
        return () -> Math.acos(getDouble());
    }

    default DoubleSupplier atan() {
        return () -> Math.atan(getDouble());
    }

    default DoubleSupplier atan2(DoubleSupplier supplier) {
        return () -> Math.atan2(getDouble(), supplier.getDouble());
    }

    default DoubleSupplier sinh() {
        return () -> Math.sinh(getDouble());
    }

    default DoubleSupplier cosh() {
        return () -> Math.cosh(getDouble());
    }

    default DoubleSupplier tanh() {
        return () -> Math.tanh(getDouble());
    }
}
