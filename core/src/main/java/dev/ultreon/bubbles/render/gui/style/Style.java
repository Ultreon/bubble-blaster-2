package dev.ultreon.bubbles.render.gui.style;


import com.badlogic.gdx.graphics.Color;
import dev.ultreon.bubbles.render.Colors;

import static dev.ultreon.bubbles.render.Colors.brighter;
import static dev.ultreon.bubbles.render.Colors.darker;

public class Style {
    public static final Color DEFAULT_ACCENT = Colors.rgb(0, 96, 128);
    public static final Color DEFAULT_BACKGROUND = Colors.rgb(96, 96, 96);
    public static final Color DEFAULT_FOREGROUND = Colors.rgb(160, 160, 160);

    public static final StateBundle<Float> DEFAULT_BORDER_WIDTHS = new StateBundle<>(1f, 1f, 1f);
    public static final StateBundle<Color> DEFAULT_BORDER_COLORS = new StateBundle<>(brighter(brighter(DEFAULT_BACKGROUND)), brighter(DEFAULT_BACKGROUND), darker(DEFAULT_BACKGROUND));
    public static final StateBundle<Color> DEFAULT_BACKGROUND_COLORS = new StateBundle<>(brighter(brighter(DEFAULT_BACKGROUND)), brighter(DEFAULT_BACKGROUND), darker(DEFAULT_BACKGROUND));
    public static final StateBundle<Color> DEFAULT_FOREGROUND_COLORS = new StateBundle<>(brighter(brighter(DEFAULT_FOREGROUND)), brighter(DEFAULT_FOREGROUND), darker(DEFAULT_FOREGROUND));
    public static final StateBundle<Float> DEFAULT_ACTIVE_BORDER_WIDTHS = new StateBundle<>(1f, 1f, 1f);
    public static final StateBundle<Color> DEFAULT_ACTIVE_BORDER_COLORS = new StateBundle<>(brighter(brighter(DEFAULT_ACCENT)), brighter(DEFAULT_ACCENT), darker(DEFAULT_ACCENT));
    public static final StateBundle<Color> DEFAULT_ACTIVE_BACKGROUND_COLORS = new StateBundle<>(brighter(brighter(DEFAULT_BACKGROUND)), brighter(DEFAULT_BACKGROUND), darker(DEFAULT_BACKGROUND));
    public static final StateBundle<Color> DEFAULT_ACTIVE_FOREGROUND_COLORS = new StateBundle<>(brighter(brighter(DEFAULT_FOREGROUND)), brighter(DEFAULT_FOREGROUND), darker(DEFAULT_FOREGROUND));

    protected StateBundle<Float> borderWidths;
    protected StateBundle<Color> borderColors;
    protected StateBundle<Color> backgroundColors;
    protected StateBundle<Color> foregroundColors;
    protected StateBundle<Float> activeBorderWidths;
    protected StateBundle<Color> activeBorderColors;
    protected StateBundle<Color> activeBackgroundColors;
    protected StateBundle<Color> activeForegroundColors;

    public Style() {
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
        return this.backgroundColors;
    }

    public void setBackgroundColors(StateBundle<Color> backgroundColors) {
        this.backgroundColors = backgroundColors;
    }

    public StateBundle<Color> getForegroundColors() {
        return this.foregroundColors;
    }

    public void setForegroundColors(StateBundle<Color> foregroundColors) {
        this.foregroundColors = foregroundColors;
    }

    public StateBundle<Color> getBorderColors() {
        return this.borderColors;
    }

    public void setBorderColors(StateBundle<Color> borderColors) {
        this.borderColors = borderColors;
    }

    public StateBundle<Float> getBorderWidths() {
        return this.borderWidths;
    }

    public void setBorderWidths(StateBundle<Float> borderWidths) {
        this.borderWidths = borderWidths;
    }

    public StateBundle<Float> getActiveBorderWidths() {
        return this.activeBorderWidths;
    }

    public void setActiveBorderWidths(StateBundle<Float> activeBorderWidths) {
        this.activeBorderWidths = activeBorderWidths;
    }

    public StateBundle<Color> getActiveBorderColors() {
        return this.activeBorderColors;
    }

    public void setActiveBorderColors(StateBundle<Color> activeBorderColors) {
        this.activeBorderColors = activeBorderColors;
    }

    public StateBundle<Color> getActiveBackgroundColors() {
        return this.activeBackgroundColors;
    }

    public void setActiveBackgroundColors(StateBundle<Color> activeBackgroundColors) {
        this.activeBackgroundColors = activeBackgroundColors;
    }

    public StateBundle<Color> getActiveForegroundColors() {
        return this.activeForegroundColors;
    }

    public void setActiveForegroundColors(StateBundle<Color> activeForegroundColors) {
        this.activeForegroundColors = activeForegroundColors;
    }
}
