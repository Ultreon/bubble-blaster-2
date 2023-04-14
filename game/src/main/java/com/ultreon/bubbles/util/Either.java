package com.ultreon.bubbles.util;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Either<L, R> {
    private final Left<L> left;
    private final Right<R> right;

    private Either(Left<L> left, Right<R> right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(new Left<>(left), null);
    }

    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(null, new Right<>(right));
    }

    public L getLeft() {
        if (left == null) throw new NoSuchElementException("The left part of the either is not present.");
        return left.value;
    }

    public R getRight() {
        if (right == null) throw new NoSuchElementException("The right part of the either is not present.");
        return right.value;
    }

    public boolean isLeftPresent() {
        return left != null;
    }

    public boolean isRightPresent() {
        return right != null;
    }

    public void ifLeft(Consumer<L> onLeft) {
        if (left != null) onLeft.accept(left.value);
    }

    public void ifRight(Consumer<R> onRight) {
        if (right != null) onRight.accept(right.value);
    }

    public void ifLeftOrElse(Consumer<L> onLeft, Runnable runnable) {
        if (left != null) onLeft.accept(left.value);
        else runnable.run();
    }

    public void ifRightOrElse(Consumer<R> onRight, Runnable runnable) {
        if (right != null) onRight.accept(right.value);
        else runnable.run();
    }

    public L getLeftOrNull() {
        return left.value;
    }

    public R getRightOrNull() {
        return right.value;
    }

    public L getLeftOrNullOr(L other) {
        L value = left.value;
        return value == null ? other : value;
    }

    public R getRightOrNullOr(R other) {
        R value = right.value;
        return value == null ? other : value;
    }

    public L getLeftOrNullOrGet(Supplier<? extends L> other) {
        L value = left.value;
        return value == null ? other.get() : value;
    }

    public R getRightOrNullOr(Supplier<? extends R> other) {
        R value = right.value;
        return value == null ? other.get() : value;
    }

    public void ifAny(Consumer<L> onLeft, Consumer<R> onRight) {
        if (left != null) onLeft.accept(left.value);
        else if (right != null) onRight.accept(right.value);
    }

    private record Left<L>(L value) { }

    private record Right<R>(R value) { }
}
