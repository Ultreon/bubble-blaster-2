package com.ultreon.bubbles.util.position;

import com.ultreon.bubbles.util.position.util.DimensionDouble;

import java.awt.*;

@SuppressWarnings("unused")
public class AWTWidgetPercentageSize extends PercentageSize {
    private Component component;

    private static DimensionDouble getSize(Component component, double percentW, double percentH) {
        double comW = component.getWidth();
        double comH = component.getHeight();

        return new DimensionDouble(comW + percentW, comH + percentH);
    }

    public AWTWidgetPercentageSize(Component component, double percentW, double percentH) {
        super(new DimensionDouble(component.getWidth(), component.getHeight()), percentW, percentH);
        this.component = component;
    }

    @Override
    public void tick() {
        setWidth(component.getWidth() * getPercentWidth());
        setHeight(component.getHeight() * getPercentHeight());
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
        tick();
    }
}
