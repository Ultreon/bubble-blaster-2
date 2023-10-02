package com.ultreon.bubbles.settings;

import com.google.gson.annotations.SerializedName;

public class GraphicsSettings {
    @SerializedName("antialias")
    private boolean antialiasEnabled;
    @SerializedName("text-antialias")
    private boolean textAntialiasEnabled;

    public boolean isAntialiasEnabled() {
        return this.antialiasEnabled;
    }

    public boolean isTextAntialiasEnabled() {
        return this.textAntialiasEnabled;
    }

    public void setAntialiasEnabled(boolean enabled) {
        this.antialiasEnabled = enabled;
        GameSettings.save();
    }

    public void setTextAntialiasEnabled(boolean enabled) {
        this.textAntialiasEnabled = enabled;
        GameSettings.save();
    }
}
