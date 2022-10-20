package com.ultreon.commons.utilities.web;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("ClassCanBeRecord")
public class GoogleResult implements Serializable {
    private final JsonObject object;

    public GoogleResult(JsonObject object) {
        this.object = object;
    }

    public String getTitle() {
        return object.getAsJsonPrimitive("title").getAsString();
    }

    public String getHtmlTitle() {
        return object.getAsJsonPrimitive("htmlTitle").getAsString();
    }

    public String getSnippet() {
        return object.getAsJsonPrimitive("snippet").getAsString();
    }

    public String getHtmlSnippet() {
        return object.getAsJsonPrimitive("htmlSnippet").getAsString();
    }

    public URL getLink() {
        try {
            return new URL(object.getAsJsonPrimitive("link").getAsString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
