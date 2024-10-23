package dev.ultreon.bubbles.render.gui.widget;


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

    public double getY(double x) {
        return this.formula.calculate(x);
    }
}
