package com.ultreon.commons.utilities.web;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class GoogleSearchInformation implements Serializable {
    private final double searchTime;
    private final long totalResults;

    public GoogleSearchInformation(JsonObject searchInformation) {
        searchTime = searchInformation.get("searchTime").getAsDouble();
        totalResults = Long.parseLong(searchInformation.get("totalResults").getAsString());
    }

    public double getSearchTime() {
        return searchTime;
    }

    public long getTotalResults() {
        return totalResults;
    }
}
