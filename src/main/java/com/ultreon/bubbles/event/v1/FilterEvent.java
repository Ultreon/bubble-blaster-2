package com.ultreon.bubbles.event.v1;

import java.awt.image.BufferedImageOp;
import java.util.ArrayList;

@Deprecated
public class FilterEvent extends Event {
    private final ArrayList<BufferedImageOp> filters = new ArrayList<>();

    public FilterEvent() {

    }

    public void addFilter(BufferedImageOp filter) {
        this.filters.add(filter);
    }

    public ArrayList<BufferedImageOp> getFilters() {
        return filters;
    }
}
