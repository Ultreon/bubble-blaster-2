package com.ultreon.bubbles.render.screen.gui.style;

import java.awt.*;

public class Style {
    public static final Color DEFAULT_ACCENT = new Color(0, 96, 128);
    public static final Color DEFAULT_BACKGROUND = new Color(96, 96, 96);
    public static final Color DEFAULT_FOREGROUND = new Color(160, 160, 160);

    public static final StateBundle<Float> DEFAULT_BORDER_WIDTHS = new StateBundle<>(1f, 1f, 1f);
    public static final StateBundle<Color> DEFAULT_BORDER_COLORS = new StateBundle<>(DEFAULT_BACKGROUND.brighter().brighter(), DEFAULT_BACKGROUND.brighter(), DEFAULT_BACKGROUND.darker());
    public static final StateBundle<Color> DEFAULT_BACKGROUND_COLORS = new StateBundle<>(DEFAULT_BACKGROUND.brighter().brighter(), DEFAULT_BACKGROUND.brighter(), DEFAULT_BACKGROUND.darker());
    public static final StateBundle<Color> DEFAULT_FOREGROUND_COLORS = new StateBundle<>(DEFAULT_FOREGROUND.brighter().brighter(), DEFAULT_FOREGROUND.brighter(), DEFAULT_FOREGROUND.darker());
    public static final StateBundle<Float> DEFAULT_ACTIVE_BORDER_WIDTHS = new StateBundle<>(1f, 1f, 1f);
    public static final StateBundle<Color> DEFAULT_ACTIVE_BORDER_COLORS = new StateBundle<>(DEFAULT_ACCENT.brighter().brighter(), DEFAULT_ACCENT.brighter(), DEFAULT_ACCENT.darker());
    public static final StateBundle<Color> DEFAULT_ACTIVE_BACKGROUND_COLORS = new StateBundle<>(DEFAULT_BACKGROUND.brighter().brighter(), DEFAULT_BACKGROUND.brighter(), DEFAULT_BACKGROUND.darker());
    public static final StateBundle<Color> DEFAULT_ACTIVE_FOREGROUND_COLORS = new StateBundle<>(DEFAULT_FOREGROUND.brighter().brighter(), DEFAULT_FOREGROUND.brighter(), DEFAULT_FOREGROUND.darker());

    protected StateBundle<Float> borderWidths;
    protected StateBundle<Color> borderColors;
    protected StateBundle<Color> backgroundColors;
    protected StateBundle<Color> foregroundColors;
    protected StateBundle<Float> activeBorderWidths;
    protected StateBundle<Color> activeBorderColors;
    protected StateBundle<Color> activeBackgroundColors;
    protected StateBundle<Color> activeForegroundColors;

    public Style() {
//        StateBundle<Color> backgroundColors, StateBundle<Color> foregroundColors, StateBundle<Color> borderColors, StateBundle<Float> borderWidths
//        this.backgroundColors = backgroundColors;
//        this.foregroundColors = foregroundColors;
//        this.borderColors = borderColors;
//        this.borderWidths = borderWidths;
        this.borderWidths = DEFAULT_BORDER_WIDTHS;
        this.borderColors = DEFAULT_BORDER_COLORS;
        this.backgroundColors = DEFAULT_BACKGROUND_COLORS;
        this.foregroundColors = DEFAULT_FOREGROUND_COLORS;
        this.activeBorderWidths = DEFAULT_ACTIVE_BORDER_WIDTHS;
        this.activeBorderColors = DEFAULT_ACTIVE_BORDER_COLORS;
        this.activeBackgroundColors = DEFAULT_ACTIVE_BACKGROUND_COLORS;
        this.activeForegroundColors = DEFAULT_ACTIVE_FOREGROUND_COLORS;
    }

    public StateBundle<Color> getBackgroundColors() {
        return backgroundColors;
    }

    public void setBackgroundColors(StateBundle<Color> backgroundColors) {
        this.backgroundColors = backgroundColors;
    }

    public StateBundle<Color> getForegroundColors() {
        return foregroundColors;
    }

    public void setForegroundColors(StateBundle<Color> foregroundColors) {
        this.foregroundColors = foregroundColors;
    }

    public StateBundle<Color> getBorderColors() {
        return borderColors;
    }

    public void setBorderColors(StateBundle<Color> borderColors) {
        this.borderColors = borderColors;
    }

    public StateBundle<Float> getBorderWidths() {
        return borderWidths;
    }

    public void setBorderWidths(StateBundle<Float> borderWidths) {
        this.borderWidths = borderWidths;
    }

    public StateBundle<Float> getActiveBorderWidths() {
        return activeBorderWidths;
    }

    public void setActiveBorderWidths(StateBundle<Float> activeBorderWidths) {
        this.activeBorderWidths = activeBorderWidths;
    }

    public StateBundle<Color> getActiveBorderColors() {
        return activeBorderColors;
    }

    public void setActiveBorderColors(StateBundle<Color> activeBorderColors) {
        this.activeBorderColors = activeBorderColors;
    }

    public StateBundle<Color> getActiveBackgroundColors() {
        return activeBackgroundColors;
    }

    public void setActiveBackgroundColors(StateBundle<Color> activeBackgroundColors) {
        this.activeBackgroundColors = activeBackgroundColors;
    }

    public StateBundle<Color> getActiveForegroundColors() {
        return activeForegroundColors;
    }

    public void setActiveForegroundColors(StateBundle<Color> activeForegroundColors) {
        this.activeForegroundColors = activeForegroundColors;
    }
}
