package com.ultreon.bubbles.common;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.exceptions.SyntaxException;
import com.ultreon.commons.lang.Pair;
import org.checkerframework.common.reflection.qual.NewInstance;
import org.checkerframework.common.value.qual.MinLen;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Deprecated
public final class Identifier {
    @SerializedName("id")
    private final @NotNull String location;
    @SerializedName("name")
    private final @NotNull String path;

    public Identifier(@NotNull String location, @NotNull String path) {
        Identifier.testLocation(location);
        Identifier.testPath(path);

        this.location = location;
        this.path = path;
    }

    public Identifier(@MinLen(3) @NotNull String name) {
        var split = name.split(":", 2);
        if (split.length == 2) {
            this.location = Identifier.testLocation(split[0]);
            this.path = Identifier.testPath(split[1]);
        } else {
            this.location = BubbleBlaster.NAMESPACE;
            this.path = Identifier.testPath(name);
        }
    }

    @NotNull
    @NewInstance
    @Contract("_ -> new")
    public static Identifier parse(
            @NotNull String name) {
        return new Identifier(name);
    }

    @Nullable
    @Contract("null -> null")
    public static Identifier tryParse(@MinLen(3) @Nullable String name) {
        if (name == null) return null;

        try {
            return new Identifier(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Contract("_ -> param1")
    public static String testLocation(String location) {
        if (!Pattern.matches("([a-z\\d_]+)(\\.[a-z][a-z\\d_]+)*", location)) {
            throw new SyntaxException("Location is invalid: " + location);
        }
        return location;
    }

    @Contract("_ -> param1")
    public static @NotNull String testPath(String path) {
        if (!Pattern.matches("([a-z_.\\d]+)(/[a-z_.\\d]+)*", path)) {
            throw new SyntaxException("Path is invalid: " + path);
        }
        return path;
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        var that = (Identifier) o;
        return this.location.equals(that.location) && this.path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.location, this.path);
    }

    @Pure
    @NotNull
    @Override
    @NewInstance
    @Contract(pure = true)
    public String toString() {
        return this.location + ":" + this.path;
    }

    /**
     * @return object location (the mod id / namespace).
     */
    @Pure
    @NotNull
    @Contract(pure = true)
    public String location() {
        return this.location;
    }

    /**
     * @return object path.
     */
    @Pure
    @NotNull
    @Contract(pure = true)
    public String path() {
        return this.path;
    }

    @NewInstance
    @Contract("_ -> new")
    public Identifier withLocation(String location) {
        return new Identifier(location, this.path);
    }

    @NewInstance
    @Contract("_ -> new")
    public Identifier withPath(String path) {
        return new Identifier(this.location, path);
    }

    @NewInstance
    @Contract("_ -> new")
    public Identifier mapLocation(Function<String, String> location) {
        return new Identifier(location.apply(this.location), this.path);
    }

    @NewInstance
    @Contract("_ -> new")
    public Identifier mapPath(Function<String, String> path) {
        return new Identifier(this.location, path.apply(this.path));
    }

    @NewInstance
    @Contract("_, _ -> new")
    public Identifier map(Function<String, String> path, Function<String, String> location) {
        return new Identifier(location.apply(this.location), path.apply(this.path));
    }

    public <T> T reduce(BiFunction<String, String, T> func) {
        return func.apply(this.location, this.path);
    }

    @Pure
    @NotNull
    @NewInstance
    @Unmodifiable
    @Contract(value = "-> new", pure = true)
    public List<String> toList() {
        return List.of(this.location, this.path);
    }

    @NotNull
    @NewInstance
    @Contract(" -> new")
    public ArrayList<String> toArrayList() {
        return Lists.newArrayList(this.location, this.path);
    }

    @Pure
    @NotNull
    @UnmodifiableView
    @Contract(pure = true)
    public Collection<String> toCollection() {
        return this.toList();
    }

    @Pure
    @NotNull
    @NewInstance
    @Contract(value = " -> new", pure = true)
    public Pair<String, String> toPair() {
        return new Pair<>(this.location, this.path);
    }

    @Pure
    @NotNull
    @NewInstance
    @Contract(value = " -> new", pure = true)
    public String[] toArray() {
        return new String[]{this.location, this.path};
    }
}
