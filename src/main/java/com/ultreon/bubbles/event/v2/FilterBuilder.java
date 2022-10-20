package com.ultreon.bubbles.event.v2;

import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterBuilder {
    private final ArrayList<BufferedImageOp> filters = new ArrayList<>();

    public void addFilter(BufferedImageOp filter) {
        this.filters.add(filter);
    }

    public List<BufferedImageOp> getFilters() {
        return Collections.unmodifiableList(filters);
    }
}
