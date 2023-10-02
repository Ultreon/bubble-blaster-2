package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@Deprecated
@FunctionalInterface
public interface DoubleSupplier extends Supplier<Double> {
    @Deprecated
    @Override
    default Double get() {
        return this.getDouble();
    }

    double getDouble();

    default DoubleSupplier add(DoubleSupplier supplier) {
        return () -> this.getDouble() + supplier.getDouble();
    }

    default DoubleSupplier sub(DoubleSupplier supplier) {
        return () -> this.getDouble() - supplier.getDouble();
    }

    default DoubleSupplier mul(DoubleSupplier supplier) {
        return () -> this.getDouble() * supplier.getDouble();
    }

    default DoubleSupplier div(DoubleSupplier supplier) {
        return () -> this.getDouble() / supplier.getDouble();
    }

    default DoubleSupplier mod(DoubleSupplier supplier) {
        return () -> this.getDouble() % supplier.getDouble();
    }

    default DoubleSupplier pow(DoubleSupplier supplier) {
        return () -> Math.pow(this.getDouble(), supplier.getDouble());
    }

    default DoubleSupplier sqrt() {
        return () -> Math.sqrt(this.getDouble());
    }

    default LongSupplier round() {
        return () -> Math.round(this.getDouble());
    }

    default DoubleSupplier roundDouble() {
        return () -> (double) Math.round(this.getDouble());
    }

    default DoubleSupplier sin() {
        return () -> Math.sin(this.getDouble());
    }

    default DoubleSupplier cos() {
        return () -> Math.cos(this.getDouble());
    }

    default DoubleSupplier tan() {
        return () -> Math.tan(this.getDouble());
    }

    default DoubleSupplier asin() {
        return () -> Math.asin(this.getDouble());
    }

    default DoubleSupplier acos() {
        return () -> Math.acos(this.getDouble());
    }

    default DoubleSupplier atan() {
        return () -> Math.atan(this.getDouble());
    }

    default DoubleSupplier atan2(DoubleSupplier supplier) {
        return () -> Math.atan2(this.getDouble(), supplier.getDouble());
    }

    default DoubleSupplier sinh() {
        return () -> Math.sinh(this.getDouble());
    }

    default DoubleSupplier cosh() {
        return () -> Math.cosh(this.getDouble());
    }

    default DoubleSupplier tanh() {
        return () -> Math.tanh(this.getDouble());
    }
}
