package com.ultreon.bubbles.theme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;

public class Theme {
    private Color menuLabelColor;
    private Color accent;
    private Color accentAlt;
    private boolean gradient;
    private boolean movingGradient;

    public Color getMenuLabelColor() {
        return menuLabelColor;
    }

    public void setMenuLabelColor(Color color) {
        this.menuLabelColor = color;
    }

    public Color getAccent() {
        return accent;
    }

    public void setAccent(Color accent) {
        this.accent = accent;
    }

    public Color getAccentAlt() {
        return accentAlt;
    }

    public void setAccentAlt(Color accentAlt) {
        this.accentAlt = accentAlt;
    }

    public boolean isGradient() {
        return gradient;
    }

    public void setGradient(boolean gradient) {
        this.gradient = gradient;
    }

    public boolean isMovingGradient() {
        return movingGradient;
    }

    public void setMovingGradient(boolean movingGradient) {
        this.movingGradient = movingGradient;
    }
}
