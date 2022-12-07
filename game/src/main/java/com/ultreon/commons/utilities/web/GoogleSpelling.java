package com.ultreon.commons.utilities.web;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class GoogleSpelling implements Serializable {
    private final String correctedSpelling;

    public GoogleSpelling(JsonObject jsonObject) {
        correctedSpelling = jsonObject.get("correctedSpelling").getAsString();
    }

    public String getCorrectedSpelling() {
        return correctedSpelling;
    }
}
