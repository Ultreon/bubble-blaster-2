package com.ultreon.commons.lang;

import java.util.Objects;

/**
 * This is an object for having 2 values / objects inside one object, or in other words having a pair create objects.
 *
 * @param <F> first object,
 * @param <S> second object.
 * @author Qboi
 * @since 0.0.0
 */
@Deprecated
public class Pair<F, S> implements Cloneable {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(getFirst(), pair.getFirst()) && Objects.equals(getSecond(), pair.getSecond());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst(), getSecond());
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
    }

    @Override
    protected Pair<F, S> clone() throws CloneNotSupportedException {
        super.clone();
        return new Pair<F, S>(getFirst(), getSecond());
    }
}
