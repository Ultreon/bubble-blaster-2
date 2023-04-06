package com.ultreon.commons.utilities.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public final class GoogleResults extends ArrayList<GoogleResult> implements Serializable {
    private Gson gson;
    private URL requestUrl;
    private boolean valid;
    private Throwable exception;
    private final GoogleSearch googleSearch;
    private JsonObject jsonObject;

    GoogleResults(GoogleSearch googleSearch) {
        this.googleSearch = googleSearch;
    }

    public Gson getGson() {
        return gson;
    }

    void setGson(Gson gson) {
        this.gson = gson;
    }

    public URL getRequestUrl() {
        return requestUrl;
    }

    void setRequestUrl(URL url) {
        this.requestUrl = url;
    }

    public boolean isValid() {
        return valid;
    }

    void setInvalid(Throwable exception) {
        this.exception = exception;
        valid = false;
    }

    public Throwable getException() {
        return exception;
    }

    public GoogleSearch getGoogleSearch() {
        return googleSearch;
    }

    void setJsonObject(JsonObject object) {
        this.jsonObject = object;
    }

    public String getQuery() {
        return googleSearch.getQuery();
    }

    private JsonObject getQueries() {
        return jsonObject.getAsJsonObject("queries");
    }

    private JsonObject getRequest() {
        return getQueries().getAsJsonArray("request").get(0).getAsJsonObject();
    }

    public String getRequestTitle() {
        return getRequest().getAsJsonPrimitive("title").getAsString();
    }

    public long getTotalResults() {
        return Long.parseLong(getRequest().getAsJsonPrimitive("totalResults").getAsString());
    }

    public long getResultCount() {
        return Long.parseLong(getRequest().getAsJsonPrimitive("count").getAsString());
    }

    public long getStartIndex() {
        return getRequest().getAsJsonPrimitive("count").getAsLong();
    }

    public String getInputEncoding() {
        return getRequest().getAsJsonPrimitive("inputEncoding").getAsString();
    }

    public String getOutputEncoding() {
        return getRequest().getAsJsonPrimitive("outputEncoding").getAsString();
    }

    public Boolean isSafeSearch() {
        String text = getRequest().getAsJsonPrimitive("outputEncoding").getAsString();
        if (Objects.equals(text, "on"))
            return true;
        else if (Objects.equals(text, "off"))
            return false;
        else
            return false;
    }

    public GoogleSearchInformation getSearchInformation() {
        return new GoogleSearchInformation(jsonObject.getAsJsonObject("searchInformation"));
    }

    public @NotNull GoogleSpelling getCorrectedSpelling() {
        return new GoogleSpelling(jsonObject.getAsJsonObject("spelling"));
    }

    @Nullable
    public GoogleSearch nextPage() {
        if (!getQueries().has("nextPage")) {
            return null;
        }

        JsonObject json = getQueries().getAsJsonArray("nextPage").get(0).getAsJsonObject();
        long count = getGoogleSearch().getCount();
        long startIndex = json.getAsJsonPrimitive("startIndex").getAsLong();

        return new GoogleSearch(getQuery(), count, startIndex, isSafeSearch());
    }

    @Nullable
    public GoogleSearch previousPage() {
        if (!getQueries().has("previousPage")) {
            return null;
        }

        JsonObject json = getQueries().getAsJsonArray("previousPage").get(0).getAsJsonObject();
        long count = getGoogleSearch().getCount();
        long startIndex = json.getAsJsonPrimitive("startIndex").getAsLong();

        return new GoogleSearch(getQuery(), count, startIndex, isSafeSearch());
    }

    public boolean hasNextPage() {
        return getQueries().has("nextPage");
    }

    public boolean hasPreviousPage() {
        return getQueries().has("previousPage");
    }
}
