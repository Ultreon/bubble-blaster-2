package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.config.Config;
import com.ultreon.bubbles.input.DesktopInput;
import com.ultreon.bubbles.util.Numbers;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.commons.v0.size.IntSize;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.text.v1.TextObject;
import org.checkerframework.com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleConsumer;

public class DecimalNumberInput extends TextEntry {
    private static final DoubleConsumer DEFAULT_CALLBACK = v -> {};

    // Value
    private double value;
    private double min;
    private double max;
    private DoubleConsumer callback = DEFAULT_CALLBACK;

    private DecimalNumberInput(Rectangle bounds, double value, double min, double max) {
        this((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, value, min, max);
    }

    private DecimalNumberInput(int x, int y, int width, int height, double value, double min, double max) {
        super(x, y, width, height);
        this.setValueAndRange(value, min, max);
    }

    private void increment() {
        this.value = Mth.clamp(this.value + this.getChangeAmount(), this.min, this.max);
        this.text = Double.toString(this.value);
        this.cursorIndex = this.text.length();
        this.callback.accept(this.value);
    }

    private void decrement() {
        this.value = Mth.clamp(this.value - this.getChangeAmount(), this.min, this.max);
        this.text = Double.toString(this.value);
        this.cursorIndex = this.text.length();
        this.callback.accept(this.value);
    }

    protected double getChangeAmount() {
        if (DesktopInput.isAltDown() && DesktopInput.isCtrlDown() && DesktopInput.isShiftDown()) return .001;
        if (DesktopInput.isAltDown() && DesktopInput.isCtrlDown()) return .1;
        if (DesktopInput.isAltDown() && DesktopInput.isShiftDown()) return .01;
        if (DesktopInput.isCtrlDown() && DesktopInput.isShiftDown()) return 1000;
        if (DesktopInput.isCtrlDown()) return 10;
        if (DesktopInput.isShiftDown()) return 100;
        return 1;
    }

    public void setValueAndRange(double value, double min, double max) {
        this.value = Mth.clamp(value, min, max);
        this.text = Double.toString(this.value);
        this.cursorIndex = this.text.length();

        this.min = min;
        this.max = max;

        this.revalidate();
    }

    @Override
    public boolean keyPress(int keyCode) {
        if (!this.isFocused()) return false;

        if (keyCode == Input.Keys.BACKSPACE) {
            if (this.cursorIndex == 0) return false;
            String leftText = this.text.substring(0, this.cursorIndex - 1);
            String rightText = this.text.substring(this.cursorIndex);

            this.text = leftText + rightText;

            this.cursorIndex--;
            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            this.revalidate();
            return true;
        }

        if (keyCode == Input.Keys.FORWARD_DEL) {
            if (this.cursorIndex >= this.text.length()) return false;
            String leftText = this.text.substring(0, this.cursorIndex);
            String rightText = this.text.substring(this.cursorIndex + 1);

            this.text = leftText + rightText;
            return true;
        }

        if (keyCode == Input.Keys.LEFT) {
            this.cursorIndex--;
            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            this.revalidate();
            return true;
        }

        if (keyCode == Input.Keys.RIGHT) {
            this.cursorIndex++;
            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            this.revalidate();
            return true;
        }

        if (keyCode == Input.Keys.ENTER || keyCode == Input.Keys.NUMPAD_ENTER) {
            this.validateNumber();
            return true;
        }

        if (keyCode == Input.Keys.UP) {
            this.increment();
            return true;
        }

        if (keyCode == Input.Keys.DOWN) {
            this.decrement();
            return true;
        }

        return false;
    }

    @Override
    public boolean charType(char character) {
        if (!this.isFocused()) return false;

        if ("0123456789.".contains(Character.toString(character))) {
            if (character == '.' && this.text.contains(".")) return false;
            if (character == '-' && this.text.startsWith("-")) {
                this.text = this.text.substring(1);
                this.cursorIndex = Mth.clamp(this.cursorIndex - 1, 0, this.text.length());
                return true;
            }
            if (character == '-' && !this.text.startsWith("-")) {
                this.text = "-" + this.text;
                this.cursorIndex = Mth.clamp(this.cursorIndex + 1, 0, this.text.length());
                return true;
            }

            String leftText = this.text.substring(0, this.cursorIndex);
            String rightText = this.text.substring(this.cursorIndex);

            this.text = leftText + character + rightText;
            this.revalidate();

            this.cursorIndex++;
            this.cursorIndex = Mth.clamp(this.cursorIndex + 1, 0, this.text.length());
            return true;
        }
        return false;
    }

    protected void validateNumber() {
        Double parsed = Numbers.tryParseDouble(this.text);
        if (parsed == null) {
            this.text = Double.toString(this.value);
            return;
        }

        this.value = Mth.clamp(parsed, this.min, this.max);
        this.text = parsed.toString();
        this.cursorIndex = this.text.length();
        this.callback.accept(parsed);
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();

        this.validateNumber();
    }

    @Override
    public Input.OnscreenKeyboardType getOnscreenKeyboardType() {
        return Input.OnscreenKeyboardType.NumberPad;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = Mth.clamp(value, this.min, this.max);
        this.text = Double.toString(this.value);
    }

    public double getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
        this.value = Mth.clamp(this.value, min, this.max);
        this.text = Double.toString(this.value);
    }

    public double getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
        this.value = Mth.clamp(this.value, this.min, max);
        this.text = Double.toString(this.value);
    }

    public void setEntryWidth(int width) {
        if (width < 0) throw new IllegalArgumentException("Entry width should be non-negative.");
        this.entryWidth = width;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setCallback(DoubleConsumer callback) {
        if (callback == null) callback = DEFAULT_CALLBACK;
        this.callback = callback;
    }

    public DoubleConsumer getCallback() {
        return this.callback;
    }

    public static class Builder {
        public Rectangle bounds = null;
        private double value;
        private double min;
        private double max;
        private int entryWidth = -1;
        private TextObject label = TextObject.EMPTY;
        private DoubleConsumer callback;

        protected Builder() {

        }

        public DecimalNumberInput build() {
            if (this.bounds == null) throw new IllegalArgumentException("Missing bounds for creating number slider.");
            if (this.entryWidth == -1) throw new IllegalArgumentException("Missing entry width for creating number slider.");

            DecimalNumberInput numberSlider = new DecimalNumberInput(this.bounds, this.value, this.min, this.max);
            numberSlider.setEntryWidth(this.entryWidth);
            numberSlider.setLabel(this.label);
            numberSlider.setCallback(this.callback);
            return numberSlider;
        }

        public @This Builder bounds(Rectangle bounds) {
            this.bounds = bounds;
            return this;
        }

        public @This Builder bounds(int x, int y, int width, int height) {
            this.bounds = new Rectangle(x, y, width, height);
            if (this.entryWidth == -1) this.entryWidth(width);
            return this;
        }

        public @This Builder bounds(Vec2i pos, IntSize size) {
            this.bounds = new Rectangle(pos.x, pos.y, size.width(), size.height());
            if (this.entryWidth == -1) this.entryWidth(size.width());
            return this;
        }

        @CanIgnoreReturnValue
        public @This Builder entryWidth(int width) {
            if (width < 0) throw new IllegalArgumentException("Entry width should be non-negative.");
            this.entryWidth = width;
            return this;
        }

        public @This Builder label(@Nullable String label) {
            return this.label(TextObject.nullToEmpty(label));
        }

        public @This Builder label(TextObject label) {
            this.label = label;
            return this;
        }

        public Builder callback(DoubleConsumer callback) {
            this.callback = callback;
            return this;
        }

        public @This Builder value(double value, double min, double max) {
            this.value = value;
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder value(Config.DoubleEntry entry) {
            return this.value(entry.get(), entry.getMinValue(), entry.getMaxValue());
        }

        public Builder value(Config.FloatEntry entry) {
            return this.value(entry.get(), entry.getMinValue(), entry.getMaxValue());
        }
    }
}
