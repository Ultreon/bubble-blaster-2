package com.ultreon.bubbles.common;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.exceptions.SyntaxException;
import com.ultreon.commons.lang.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.reflection.qual.NewInstance;
import org.checkerframework.common.value.qual.MinLen;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

@SuppressWarnings({"unused"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Identifier {
    @SerializedName("id")
    private final @NonNull String location;
    @SerializedName("name")
    private final @NonNull String path;

    public Identifier(
            @NonNull String path,
            @NonNull String location) {
        if (!Pattern.matches("^[A-Za-z\\d_/.]*((\\.[a-z\\d_]*)?)$", path)) {
            throw new SyntaxException("Path contains illegal characters: " + path);
        }

        if (!Pattern.matches("^([a-z][a-z\\d_]{2,})(\\.[a-z][a-z\\d_]{2,})*$", location)) {
            throw new SyntaxException("Location contains illegal characters: " + location);
        }

        this.location = location;
        this.path = path;
    }

    public Identifier(
            @MinLen(3)
            @NonNull String name) {
        String[] split = name.split("@", 1);
        if (split.length == 2) {
            this.path = testPath(split[0]);
            this.location = testLocation(split[1]);
        } else {
            this.path = testPath(name);
            this.location = BubbleBlaster.NAMESPACE;
        }
    }

    @NonNull
    @NewInstance
    @Contract("_ -> new")
    public static Identifier parse(
            @NonNull String name) {
        return new Identifier(name);
    }

    @Nullable
    @Contract("null -> null")
    public static Identifier tryParse(
            @MinLen(3)
            @Nullable String name) {
        if (name == null) return null;
        try {
            return new Identifier(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Contract("_ -> param1")
    public static String testLocation(String location) {
        if (!Pattern.matches("([a-z][a-z\\d_]{2,})(\\.[a-z][a-z\\d_]{2,})+", location)) {
            throw new SyntaxException("Location is invalid: " + location);
        }
        return location;
    }

    @Contract("_ -> param1")
    public static @NonNull String testPath(String path) {
        if (!Pattern.matches("([a-z_.\\d]{2,})(/[a-z_.\\d]{2,})*", path)) {
            throw new SyntaxException("Path is invalid: " + path);
        }
        return path;
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return location.equals(that.location) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, path);
    }

    @Pure
    @NonNull
    @Override
    @NewInstance
    @Contract(pure = true)
    public String toString() {
        return path + "@" + location;
    }

    /**
     * @return object location (the mod id / namespace).
     */
    @Pure
    @NonNull
    @Contract(pure = true)
    public String location() {
        return location;
    }

    /**
     * @return object path.
     */
    @Pure
    @NonNull
    @Contract(pure = true)
    public String path() {
        return path;
    }

    @NewInstance
    @Contract("_ -> new")
    public Identifier withLocation(String location) {
        return new Identifier(path, location);
    }

    @NewInstance
    @Contract("_ -> new")
    public Identifier withPath(String path) {
        return new Identifier(path, this.location);
    }

    @NewInstance
    @Contract("_ -> new")
    public Identifier mapLocation(Function<String, String> location) {
        return new Identifier(this.path, location.apply(this.location));
    }

    @NewInstance
    @Contract("_ -> new")
    public Identifier mapPath(Function<String, String> path) {
        return new Identifier(path.apply(this.path), this.location);
    }

    @NewInstance
    @Contract("_, _ -> new")
    public Identifier map(Function<String, String> path, Function<String, String> location) {
        return new Identifier(path.apply(this.path), location.apply(this.location));
    }

    public <T> T reduce(BiFunction<String, String, T> func) {
        return func.apply(location, path);
    }

    @Pure
    @NonNull
    @NewInstance
    @Unmodifiable
    @Contract(value = "-> new", pure = true)
    public List<String> toList() {
        return List.of(location, path);
    }

    @NonNull
    @NewInstance
    @Contract(" -> new")
    public ArrayList<String> toArrayList() {
        return Lists.newArrayList(location, path);
    }

    @Pure
    @NonNull
    @UnmodifiableView
    @Contract(pure = true)
    public Collection<String> toCollection() {
        return toList();
    }

    @Pure
    @NonNull
    @NewInstance
    @Contract(value = " -> new", pure = true)
    public Pair<String, String> toPair() {
        return new Pair<>(location, path);
    }

    @Pure
    @NonNull
    @NewInstance
    @Contract(value = " -> new", pure = true)
    public String[] toArray() {
        return new String[]{location, path};
    }
}
