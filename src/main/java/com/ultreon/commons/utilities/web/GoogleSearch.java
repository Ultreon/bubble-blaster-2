package com.ultreon.commons.utilities.web;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.ultreon.commons.exceptions.IllegalJsonElementInArray;
import com.ultreon.commons.exceptions.UnsafeOperationException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class GoogleSearch implements Iterable<GoogleResult>, Serializable {
    public static String API_KEY = null;
    public static final String SEARCH_ENGINE_ID = null;

    private long count;
    private long startIndex;
    private String query;
    private final HashMap<URL, GoogleResults> cache = new HashMap<>();
    private boolean safeSearch;

    public GoogleSearch(String query) {
        this(query, 10, 1);
    }

    public GoogleSearch(String query, long count, long startIndex) {
        this(query, count, startIndex, false);
    }

    @SuppressWarnings("ConstantConditions")
    public GoogleSearch(String query, long count, long startIndex, boolean safeSearch) {
        this.safeSearch = safeSearch;
        if (API_KEY == null) {
            throw new IllegalArgumentException("API_KEY is not set. Set the API_KEY static field to search.");
        }

        if (SEARCH_ENGINE_ID == null) {
            throw new IllegalArgumentException("SEARCH_ENGINE_ID is not set. Set the SEARCH_ENGINE_ID static field to search.");
        }

        this.query = query;
        this.count = count;
        this.startIndex = startIndex;
    }

    public void clearCache() {
        this.cache.clear();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    public boolean isSafeSearch() {
        return safeSearch;
    }

    public void setSafeSearch(boolean safeSearch) {
        this.safeSearch = safeSearch;
    }


//    /**
//     * Warning: Do not use in for-loop, can cause extremely large performance issues,
//     *
//     * @return the google result iterator.
//     */
//    @SuppressWarnings("Convert2Diamond")
//    @NonNull
//    @Override
//    public Iterator<GoogleResult> iterator() {
//        GoogleSearch googleSearch = new GoogleSearch(query, 1, 1, safeSearch);
//
//        return new Iterator<GoogleResult>() {
//            GoogleResults currentResults = googleSearch.getResults();
//
//            @Override
//            public boolean hasNext() {
//                return currentResults.hasNextPage();
//            }
//
//            @Override
//            public GoogleResult next() {
//                GoogleResult result = currentResults.get(0);
//                currentResults = Objects.requireNonNull(currentResults.nextPage()).getResults();
//                return result;
//            }
//        };
//    }

    @NonNull
    @Override
    public Iterator<GoogleResult> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super GoogleResult> action) {
        throw new UnsafeOperationException();
    }

    @Override
    public Spliterator<GoogleResult> spliterator() {
        return null;
    }
}
