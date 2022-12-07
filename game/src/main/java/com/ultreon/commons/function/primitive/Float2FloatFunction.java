package com.ultreon.commons.function.primitive;

import java.util.function.Function;

@FunctionalInterface
public interface Float2FloatFunction extends Function<Float, Float> {
    @Override
    @Deprecated
    default Float apply(Float aFloat) {
        return apply((float) aFloat);
    }

    float apply(float x);

    static Float2FloatFunction add(float amount) {
        return x -> x + amount;
    }

    static Float2FloatFunction sub(float amount) {
        return x -> x - amount;
    }

    static Float2FloatFunction mul(float amount) {
        return x -> x * amount;
    }

    static Float2FloatFunction div(float amount) {
        return x -> x / amount;
    }

    static Float2FloatFunction mod(float amount) {
        return x -> x % amount;
    }

    static Float2FloatFunction pow(float amount) {
        return x -> (float) Math.pow(x, amount);
    }

    static Float2FloatFunction add(FloatSupplier amount) {
        return x -> x + amount.getFloat();
    }

    static Float2FloatFunction sub(FloatSupplier amount) {
        return x -> x - amount.getFloat();
    }

    static Float2FloatFunction mul(FloatSupplier amount) {
        return x -> x * amount.getFloat();
    }

    static Float2FloatFunction div(FloatSupplier amount) {
        return x -> x / amount.getFloat();
    }

    static Float2FloatFunction mod(FloatSupplier amount) {
        return x -> x % amount.getFloat();
    }

    static Float2FloatFunction pow(FloatSupplier amount) {
        return x -> (float) Math.pow(x, amount.getFloat());
    }

    static Float2FloatFunction sqrt() {
        return x -> (float) Math.sqrt(x);
    }

    static Float2FloatFunction round() {
        return x -> (float) Math.round(x);
    }

    static Float2FloatFunction asin() {
        return x -> (float) Math.asin(x);
    }

    static Float2FloatFunction acos() {
        return x -> (float) Math.acos(x);
    }

    static Float2FloatFunction atan() {
        return x -> (float) Math.atan(x);
    }

    static Float2FloatFunction atan2(float y) {
        return x -> (float) Math.atan2(x, y);
    }

    static Float2FloatFunction sin() {
        return x -> (float) Math.sin(x);
    }

    static Float2FloatFunction cos() {
        return x -> (float) Math.cos(x);
    }

    static Float2FloatFunction tan() {
        return x -> (float) Math.tan(x);
    }

    static Float2FloatFunction sinh() {
        return x -> (float) Math.sinh(x);
    }

    static Float2FloatFunction cosh() {
        return x -> (float) Math.cosh(x);
    }

    static Float2FloatFunction tanh() {
        return x -> (float) Math.tanh(x);
    }

    static Float2FloatFunction floor() {
        return x -> (float) Math.floor(x);
    }

    static Float2FloatFunction ceil() {
        return x -> (float) Math.ceil(x);
    }

    static Float2FloatFunction log() {
        return x -> (float) Math.log(x);
    }

    static Float2FloatFunction log10() {
        return x -> (float) Math.log10(x);
    }

    static Float2FloatFunction log1p() {
        return x -> (float) Math.log1p(x);
    }

    static Float2FloatFunction ulp() {
        return Math::ulp;
    }

    static Float2FloatFunction signum() {
        return Math::signum;
    }

    static Float2FloatFunction minus() {
        return x -> -x;
    }

    static Float2FloatFunction plus() {
        return x -> +x;
    }

    static Float2FloatFunction scalb(int scaleFactor) {
        return x -> Math.scalb(x, scaleFactor);
    }
}
