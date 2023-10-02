package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@Deprecated
@FunctionalInterface
public interface ByteSupplier extends Supplier<Byte> {
    @Deprecated
    @Override
    default Byte get() {
        return this.getByte();
    }

    byte getByte();

    default ByteSupplier and(ByteSupplier supplier) {
        return () -> (byte) (this.getByte() & supplier.getByte());
    }

    default ByteSupplier or(ByteSupplier supplier) {
        return () -> (byte) (this.getByte() | supplier.getByte());
    }

    default ByteSupplier add(ByteSupplier supplier) {
        return () -> (byte) (this.getByte() + supplier.getByte());
    }

    default ByteSupplier sub(ByteSupplier supplier) {
        return () -> (byte) (this.getByte() - supplier.getByte());
    }

    default ByteSupplier mul(ByteSupplier supplier) {
        return () -> (byte) (this.getByte() * supplier.getByte());
    }

    default ByteSupplier div(ByteSupplier supplier) {
        return () -> (byte) (this.getByte() / supplier.getByte());
    }

    default ByteSupplier mod(ByteSupplier supplier) {
        return () -> (byte) (this.getByte() % supplier.getByte());
    }

    default ByteSupplier pow(ByteSupplier supplier) {
        return () -> (byte) Math.pow(this.getByte(), supplier.getByte());
    }

    default ByteSupplier sqrt() {
        return () -> (byte) Math.sqrt(this.getByte());
    }

    default ByteSupplier round() {
        return () -> (byte) Math.round(this.getByte());
    }

    default ByteSupplier sin() {
        return () -> (byte) Math.sin(this.getByte());
    }

    default ByteSupplier cos() {
        return () -> (byte) Math.cos(this.getByte());
    }

    default ByteSupplier tan() {
        return () -> (byte) Math.tan(this.getByte());
    }

    default ByteSupplier asin() {
        return () -> (byte) Math.asin(this.getByte());
    }

    default ByteSupplier acos() {
        return () -> (byte) Math.acos(this.getByte());
    }

    default ByteSupplier atan() {
        return () -> (byte) Math.atan(this.getByte());
    }

    default ByteSupplier atan2(ByteSupplier supplier) {
        return () -> (byte) Math.atan2(this.getByte(), supplier.getByte());
    }

    default ByteSupplier sinh() {
        return () -> (byte) Math.sinh(this.getByte());
    }

    default ByteSupplier cosh() {
        return () -> (byte) Math.cosh(this.getByte());
    }

    default ByteSupplier tanh() {
        return () -> (byte) Math.tanh(this.getByte());
    }
}
