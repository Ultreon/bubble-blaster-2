package com.ultreon.bubbles.render.gui.widget;

import java.awt.geom.Point2D;

public class Formula {
    private IFormula formula;

    public Formula(IFormula formula) {
        this.formula = formula;
    }

    public IFormula getFormula() {
        return this.formula;
    }

    public void setFormula(IFormula formula) {
        this.formula = formula;
    }

    public Point2D.Double getPoint(double x) {
        var y = this.formula.calculate(x);
        return new Point2D.Double(x, y);
    }

    public double getY(double x) {
        return this.formula.calculate(x);
    }
}
