package com.ultreon.bubbles.render.gui;

import java.awt.geom.Point2D;

public class QFormula {
    private IFormula formula;

    public QFormula(IFormula formula) {
        this.formula = formula;
    }

    public IFormula getFormula() {
        return formula;
    }

    public void setFormula(IFormula formula) {
        this.formula = formula;
    }

    public Point2D.Double getPoint(double x) {
        double y = this.formula.calculate(x);
        return new Point2D.Double(x, y);
    }

    public double getY(double x) {
        return this.formula.calculate(x);
    }
}
