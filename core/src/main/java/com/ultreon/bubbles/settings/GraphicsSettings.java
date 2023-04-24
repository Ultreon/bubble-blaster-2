package com.ultreon.bubbles.settings;

import com.google.gson.annotations.SerializedName;

public class GraphicsSettings {
    @SerializedName("antialias")
    private boolean antialiasEnabled;
    @SerializedName("text-antialias")
    private boolean textAntialiasEnabled;

    public boolean isAntialiasEnabled() {
        return antialiasEnabled;
    }

    public boolean isTextAntialiasEnabled() {
        return textAntialiasEnabled;
    }

    public void setAntialiasEnabled(boolean enabled) {
        antialiasEnabled = enabled;
        GameSettings.save();
    }

    public void setTextAntialiasEnabled(boolean enabled) {
        textAntialiasEnabled = enabled;
        GameSettings.save();
    }
}
