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
        if (this.left == null) throw new NoSuchElementException("The left part of the either is not present.");
        return this.left.value;
    }

    public R getRight() {
        if (this.right == null) throw new NoSuchElementException("The right part of the either is not present.");
        return this.right.value;
    }

    public boolean isLeftPresent() {
        return this.left != null;
    }

    public boolean isRightPresent() {
        return this.right != null;
    }

    public void ifLeft(Consumer<L> onLeft) {
        if (this.left != null) onLeft.accept(this.left.value);
    }

    public void ifRight(Consumer<R> onRight) {
        if (this.right != null) onRight.accept(this.right.value);
    }

    public void ifLeftOrElse(Consumer<L> onLeft, Runnable runnable) {
        if (this.left != null) onLeft.accept(this.left.value);
        else runnable.run();
    }

    public void ifRightOrElse(Consumer<R> onRight, Runnable runnable) {
        if (this.right != null) onRight.accept(this.right.value);
        else runnable.run();
    }

    public L getLeftOrNull() {
        return this.left.value;
    }

    public R getRightOrNull() {
        return this.right.value;
    }

    public L getLeftOrNullOr(L other) {
        L value = this.left.value;
        return value == null ? other : value;
    }

    public R getRightOrNullOr(R other) {
        R value = this.right.value;
        return value == null ? other : value;
    }

    public L getLeftOrNullOrGet(Supplier<? extends L> other) {
        L value = this.left.value;
        return value == null ? other.get() : value;
    }

    public R getRightOrNullOr(Supplier<? extends R> other) {
        R value = this.right.value;
        return value == null ? other.get() : value;
    }

    public void ifAny(Consumer<L> onLeft, Consumer<R> onRight) {
        if (this.left != null) onLeft.accept(this.left.value);
        else if (this.right != null) onRight.accept(this.right.value);
    }

    private record Left<L>(L value) { }

    private record Right<R>(R value) { }
}
