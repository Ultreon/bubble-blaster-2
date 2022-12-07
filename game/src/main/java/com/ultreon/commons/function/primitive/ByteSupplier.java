package com.ultreon.commons.function.primitive;

import java.util.function.Supplier;

@FunctionalInterface
public interface ByteSupplier extends Supplier<Byte> {
    @Deprecated
    @Override
    default Byte get() {
        return getByte();
    }

    byte getByte();

    default ByteSupplier and(ByteSupplier supplier) {
        return () -> (byte) (getByte() & supplier.getByte());
    }

    default ByteSupplier or(ByteSupplier supplier) {
        return () -> (byte) (getByte() | supplier.getByte());
    }

    default ByteSupplier add(ByteSupplier supplier) {
        return () -> (byte) (getByte() + supplier.getByte());
    }

    default ByteSupplier sub(ByteSupplier supplier) {
        return () -> (byte) (getByte() - supplier.getByte());
    }

    default ByteSupplier mul(ByteSupplier supplier) {
        return () -> (byte) (getByte() * supplier.getByte());
    }

    default ByteSupplier div(ByteSupplier supplier) {
        return () -> (byte) (getByte() / supplier.getByte());
    }

    default ByteSupplier mod(ByteSupplier supplier) {
        return () -> (byte) (getByte() % supplier.getByte());
    }

    default ByteSupplier pow(ByteSupplier supplier) {
        return () -> (byte) Math.pow(getByte(), supplier.getByte());
    }

    default ByteSupplier sqrt() {
        return () -> (byte) Math.sqrt(getByte());
    }

    default ByteSupplier round() {
        return () -> (byte) Math.round(getByte());
    }

    default ByteSupplier sin() {
        return () -> (byte) Math.sin(getByte());
    }

    default ByteSupplier cos() {
        return () -> (byte) Math.cos(getByte());
    }

    default ByteSupplier tan() {
        return () -> (byte) Math.tan(getByte());
    }

    default ByteSupplier asin() {
        return () -> (byte) Math.asin(getByte());
    }

    default ByteSupplier acos() {
        return () -> (byte) Math.acos(getByte());
    }

    default ByteSupplier atan() {
        return () -> (byte) Math.atan(getByte());
    }

    default ByteSupplier atan2(ByteSupplier supplier) {
        return () -> (byte) Math.atan2(getByte(), supplier.getByte());
    }

    default ByteSupplier sinh() {
        return () -> (byte) Math.sinh(getByte());
    }

    default ByteSupplier cosh() {
        return () -> (byte) Math.cosh(getByte());
    }

    default ByteSupplier tanh() {
        return () -> (byte) Math.tanh(getByte());
    }
}
