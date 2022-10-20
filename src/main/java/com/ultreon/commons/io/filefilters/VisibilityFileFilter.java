package com.ultreon.commons.io.filefilters;

import java.io.File;
import java.io.FileFilter;
import java.util.Objects;

public final class VisibilityFileFilter implements FileFilter {
    private final boolean filterVisible;

    public VisibilityFileFilter(boolean filterVisible) {
        this.filterVisible = filterVisible;
    }

    @Override
    public boolean accept(File pathname) {
        return filterVisible != pathname.isHidden();
    }

    public boolean filterVisible() {
        return filterVisible;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        VisibilityFileFilter that = (VisibilityFileFilter) obj;
        return this.filterVisible == that.filterVisible;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterVisible);
    }

    @Override
    public String toString() {
        return "VisibilityFileFilter[" +
                "filterVisible=" + filterVisible + ']';
    }

}
