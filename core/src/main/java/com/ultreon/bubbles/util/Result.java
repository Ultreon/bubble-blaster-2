package com.ultreon.bubbles.util;

import java.util.function.Consumer;

public abstract class Result<T> {
    private final T value;
    private final Exception exception;

    private Result(T value, Exception exception) {
        this.value = value;
        this.exception = exception;
    }

    public static <T> Result<T> success(T value) {
        return new Result.Success<>(value);
    }

    public static <T> Result<T> failure(Exception exception) {
        return new Result.Failure<>(exception);
    }

    public abstract boolean isSuccess();

    public final boolean isFailure() {
        return !this.isSuccess();
    }

    public T getValue() {
        if (this.isSuccess())
            return this.value;
        else
            throw new IllegalStateException("Cannot get value from a failure result.");
    }

    public Exception getException() {
        if (this.isFailure())
            return this.exception;
        else
            throw new IllegalStateException("Cannot get exception from a success result.");
    }

    public void ifFailure(Consumer<Exception> valueConsumer) {
        if (this.isSuccess())
            valueConsumer.accept(this.exception);
    }

    public void ifSuccess(Consumer<T> valueConsumer) {
        if (this.isSuccess())
            valueConsumer.accept(this.value);
    }

    private static class Success<T> extends Result<T> {
        public Success(T value) {
            super(value, null);
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    private static class Failure<T> extends Result<T> {
        public Failure(Exception exception) {
            super(null, exception);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
    }
}
