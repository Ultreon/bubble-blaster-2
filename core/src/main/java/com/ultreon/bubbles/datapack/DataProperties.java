package com.ultreon.bubbles.datapack;

public class DataProperties {
    private String id;

    private DataProperties() {

    }

    public String getId() {
        return id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;

        private Builder() {

        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public DataProperties build() {
            DataProperties properties = new DataProperties();
            properties.id = id;
            return properties;
        }
    }
}
