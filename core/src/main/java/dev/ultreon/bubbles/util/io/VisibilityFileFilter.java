package dev.ultreon.bubbles.util.io;

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
        return this.filterVisible != pathname.isHidden();
    }

    public boolean filterVisible() {
        return this.filterVisible;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VisibilityFileFilter) obj;
        return this.filterVisible == that.filterVisible;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.filterVisible);
    }

    @Override
    public String toString() {
        return "VisibilityFileFilter[" +
                "filterVisible=" + this.filterVisible + ']';
    }

}
