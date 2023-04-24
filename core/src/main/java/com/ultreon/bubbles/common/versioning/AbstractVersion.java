package com.ultreon.bubbles.common.versioning;

/**
 * Base version instance.
 * @param <T> the subclass type.
 */
public abstract class AbstractVersion<T extends AbstractVersion<T>> implements Comparable<T> {
    public abstract boolean isStable();

    public abstract String toString();
}
