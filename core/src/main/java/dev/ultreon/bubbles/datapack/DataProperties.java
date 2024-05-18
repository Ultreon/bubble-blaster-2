package dev.ultreon.bubbles.datapack;

public class DataProperties {
    private String id;

    private DataProperties() {

    }

    public String getId() {
        return this.id;
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
            var properties = new DataProperties();
            properties.id = this.id;
            return properties;
        }
    }
}
