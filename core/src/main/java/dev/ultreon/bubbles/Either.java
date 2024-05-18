package dev.ultreon.bubbles;

import java.util.NoSuchElementException;
import java.util.Objects;
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
        if (this.left == null) return null;
        return this.left.value;
    }

    public R getRightOrNull() {
        if (this.right == null) return null;
        return this.right.value;
    }

    public L getLeftOr(L other) {
        if (this.left == null) return other;
        var value = this.left.value;
        return value == null ? other : value;
    }

    public R getRightOr(R other) {
        if (this.right == null) return other;
        var value = this.right.value;
        return value == null ? other : value;
    }

    public L getLeftOrGet(Supplier<? extends L> other) {
        if (this.left == null) return other.get();
        var value = this.left.value;
        return value == null ? other.get() : value;
    }

    public R getRightOrGet(Supplier<? extends R> other) {
        if (this.right == null) return other.get();
        var value = this.right.value;
        return value == null ? other.get() : value;
    }

    public void ifAny(Consumer<L> onLeft, Consumer<R> onRight) {
        if (this.left != null) onLeft.accept(this.left.value);
        else if (this.right != null) onRight.accept(this.right.value);
    }

    private static final class Left<L> {
        private final L value;

        private Left(L value) {
            this.value = value;
        }

        public L value() {
            return this.value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Left) obj;
            return Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "Left[" +
                    "value=" + this.value + ']';
        }
    }

    private static final class Right<R> {
        private final R value;

        private Right(R value) {
            this.value = value;
        }

        public R value() {
            return this.value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Right) obj;
            return Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "Right[" +
                    "value=" + this.value + ']';
        }
    }
}
