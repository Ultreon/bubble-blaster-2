package com.ultreon.bubbles.datapack;

import com.google.gson.Gson;

public abstract class BaseData {
    private final DataProperties properties;

    public BaseData(DataProperties properties) {
        this.properties = properties;
    }

    public Gson getGson() {
//        resource.openStream();
        return null;
    }

    public DataProperties getProperties() {
        return this.properties;
    }
}
