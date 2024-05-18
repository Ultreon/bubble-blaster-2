package dev.ultreon.bubbles.settings;

import com.google.gson.annotations.SerializedName;

public class DebugOptions {
    @SerializedName("spaced-namespace")
    private boolean spacedNamespace = false;
    @SerializedName("spaced-enum-constants")
    private boolean spacedEnumConstants;

    public DebugOptions() {

    }

    private void save() {
        GameSettings.save();
    }

    public boolean isSpacedNamespace() {
        return this.spacedNamespace;
    }

    public void setSpacedNamespace(boolean spacedNamespace) {
        this.spacedNamespace = spacedNamespace;
    }

    public boolean isSpacedEnumConstants() {
        return this.spacedEnumConstants;
    }

    public void setSpacedEnumConstants(boolean spacedEnumConstants) {
        this.spacedEnumConstants = spacedEnumConstants;
    }
}
