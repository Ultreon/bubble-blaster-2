package dev.ultreon.bubbles.util;

import dev.ultreon.bubbles.Either;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Functions {
    public static @NotNull Optional<Exception> tryRun(@NotNull Runnable runnable) {
        try {
            runnable.run();
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    public static <T> @NotNull Either<T, Exception> tryGet(@NotNull Supplier<T> supplier) {
        try {
            return Either.left(supplier.get());
        } catch (Exception e) {
            return Either.right(e);
        }
    }

    public static <T> @NotNull Optional<Exception> tryAccept(@NotNull Consumer<T> consumer, T value) {
        try {
            consumer.accept(value);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    public static <T> @NotNull Optional<Exception> tryAccept(@NotNull Consumer<T> consumer, Supplier<T> value) {
        try {
            consumer.accept(value.get());
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    public static <T, R> @NotNull Either<R, Exception> tryApply(@NotNull Function<T, R> consumer, T value) {
        try {
            return Either.left(consumer.apply(value));
        } catch (Exception e) {
            return Either.right(e);
        }
    }

    public static <T, R> @NotNull Either<R, Exception> tryApply(@NotNull Function<T, R> consumer, Supplier<T> value) {
        try {
            return Either.left(consumer.apply(value.get()));
        } catch (Exception e) {
            return Either.right(e);
        }
    }
}
