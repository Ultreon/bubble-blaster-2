package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.config.Config;
import com.ultreon.bubbles.input.DesktopInput;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.Numbers;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.commons.v0.size.IntSize;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.text.v1.TextObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;

public class NumberSlider extends TextEntry {
    private static final IntConsumer DEFAULT_CALLBACK = i -> {};
    private final ArrowButton upButton;
    private final ArrowButton downButton;

    // Value
    private int value;
    private int min;
    private int max;
    private IntConsumer callback = DEFAULT_CALLBACK;

    private NumberSlider(Rectangle bounds, int value, int min, int max) {
        this((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, value, min, max);
    }

    public NumberSlider(int x, int y, int width, int height, int value, int min, int max) {
        super(x, y, width, height);
        this.setValueAndRange(value, min, max);
        this.setResponder(s -> {
            var i = Numbers.tryParseInt(s);
            return i != null && i >= min && i <= max;
        });

        this.upButton = new ArrowButton(0, 0, 0, 0, this::increment);
        this.downButton = new ArrowButton(0, 0, 0, 0, this::decrement);
    }

    public void setValueAndRange(int value, int min, int max) {
        this.value = Mth.clamp(value, min, max);
        this.text = Integer.toString(this.value);
        this.validText = this.text;
        this.cursorIndex = this.text.length();

        this.min = min;
        this.max = max;

        this.updateCursor();
    }

    private void increment() {
        this.value = Mth.clamp(this.value + this.getChangeAmount(), this.min, this.max);
        this.text = Integer.toString(this.value);
        this.validText = this.text;
        this.cursorIndex = this.text.length();
        this.callback.accept(this.value);
    }

    private void decrement() {
        this.value = Mth.clamp(this.value - this.getChangeAmount(), this.min, this.max);
        this.text = Integer.toString(this.value);
        this.validText = this.text;
        this.cursorIndex = this.text.length();
        this.callback.accept(this.value);
    }

    protected int getChangeAmount() {
        if (DesktopInput.isCtrlDown() && DesktopInput.isShiftDown()) return 1000;
        if (DesktopInput.isCtrlDown()) return 10;
        if (DesktopInput.isShiftDown()) return 100;
        return 1;
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (!this.isFocused()) return false;

        if (this.upButton.mousePress(x, y, button)) return true;
        return this.downButton.mousePress(x, y, button);
    }

    @Override
    public boolean keyPress(int keyCode) {
        if (!this.isFocused()) return false;

        if (keyCode == Input.Keys.BACKSPACE) {
            if (this.cursorIndex == 0) return false;
            var leftText = this.text.substring(0, this.cursorIndex - 1);
            var rightText = this.text.substring(this.cursorIndex);

            this.text = leftText + rightText;

            this.cursorIndex--;
            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            this.updateCursor();
            this.validate();
            return true;
        }

        if (keyCode == Input.Keys.FORWARD_DEL) {
            if (this.cursorIndex >= this.text.length()) return false;
            var leftText = this.text.substring(0, this.cursorIndex);
            var rightText = this.text.substring(this.cursorIndex + 1);

            this.text = leftText + rightText;
            this.validate();
            return true;
        }

        if (keyCode == Input.Keys.LEFT) {
            this.cursorIndex--;
            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            this.updateCursor();
            return true;
        }

        if (keyCode == Input.Keys.RIGHT) {
            this.cursorIndex++;
            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            this.updateCursor();
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
    public void onFocusLost() {
        super.onFocusLost();

        this.validateNumber();
    }

    @Override
    public Input.OnscreenKeyboardType getOnscreenKeyboardType() {
        return Input.OnscreenKeyboardType.NumberPad;
    }

    protected void validateNumber() {
        var parsed = Numbers.tryParseInt(this.text);
        if (parsed == null) {
            this.validText = Integer.toString(this.value);
            return;
        }

        this.value = Mth.clamp(parsed, this.min, this.max);
        this.text = parsed.toString();
        this.validText = this.text;
        this.callback.accept(parsed);
    }

    @Override
    public boolean charType(char character) {
        if (!this.isFocused()) return false;

        if (!"0123456789".contains(Character.toString(character)))
            return false;

        if (character == '-' && this.text.startsWith("-")) {
            this.text = this.text.substring(1);
            this.cursorIndex = Mth.clamp(this.cursorIndex - 1, 0, this.text.length());
            this.validate();
            return true;
        }
        if (character == '-' && !this.text.startsWith("-")) {
            this.text = "-" + this.text;
            this.cursorIndex = Mth.clamp(this.cursorIndex + 1, 0, this.text.length());
            this.validate();
            return true;
        }

        var leftText = this.text.substring(0, this.cursorIndex);
        var rightText = this.text.substring(this.cursorIndex);

        this.text = leftText + character + rightText;
        this.cursorIndex = Mth.clamp(this.cursorIndex + 1, 0, this.text.length());
        this.updateCursor();
        this.validate();
        return true;
    }

    @Override
    public void make() {
        super.make();

        this.upButton.make();
        this.downButton.make();
    }

    @Override
    public void dispose() {
        super.dispose();

        this.upButton.dispose();
        this.downButton.dispose();
    }

    @Override
    public boolean mouseClick(int x, int y, int button, int count) {
        if (this.upButton.mouseClick(x, y, button, count)) return true;
        if (this.downButton.mouseClick(x, y, button, count)) return true;
        return super.mouseClick(x, y, button, count);
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        if (this.upButton.mouseRelease(x, y, button)) return true;
        if (this.downButton.mouseRelease(x, y, button)) return true;
        return super.mouseRelease(x, y, button);
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.upButton.setX(this.x + this.width - 25);
        this.upButton.setY(this.y);
        this.upButton.setHeight(this.height / 2 - 1);
        this.upButton.setWidth(25);
        this.upButton.setUp(true);
        this.upButton.render(renderer, mouseX, mouseY, deltaTime);

        this.downButton.setX(this.x + this.width - 25);
        this.downButton.setY(this.y + this.height / 2);
        this.downButton.setHeight(this.height / 2);
        this.downButton.setWidth(25);
        this.downButton.setUp(false);
        this.downButton.render(renderer, mouseX, mouseY, deltaTime);

        final var entryX = Math.max(this.x + this.width - this.entryWidth, this.x);
        final var entryW = Math.max(Math.min(this.entryWidth, this.width) - 25, 0);
        final var labelX = this.x;
        final var labelW = Math.max(this.width - 25 - entryW, 0);

        this.drawBackground(renderer, labelX, labelW, entryX, entryW);
        this.drawText(renderer, entryX, entryW, labelX, labelW);
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = Mth.clamp(value, this.min, this.max);
        this.text = Integer.toString(this.value);
        this.cursorIndex = this.text.length();
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
        this.value = Mth.clamp(this.value, min, this.max);
        this.text = Integer.toString(this.value);
        this.cursorIndex = this.text.length();
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
        this.value = Mth.clamp(this.value, this.min, max);
        this.text = Integer.toString(this.value);
        this.cursorIndex = this.text.length();
    }

    @Override
    public void setEntryWidth(int width) {
        if (width < 0) throw new IllegalArgumentException("Entry width should be non-negative.");
        this.entryWidth = width;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setCallback(IntConsumer callback) {
        if (callback == null) callback = DEFAULT_CALLBACK;
        this.callback = callback;
    }

    public IntConsumer getCallback() {
        return this.callback;
    }

    public static class Builder extends TextEntry.Builder {
        public Rectangle bounds = null;
        private int value;
        private int min;
        private int max;
        private int entryWidth = -1;
        private TextObject label = TextObject.EMPTY;
        private IntConsumer callback;

        protected Builder() {

        }

        @Override
        public NumberSlider build() {
            if (this.bounds == null) throw new IllegalArgumentException("Missing bounds for creating number slider.");
            if (this.entryWidth == -1) throw new IllegalArgumentException("Missing entry width for creating number slider.");

            var numberSlider = new NumberSlider(this.bounds, this.value, this.min, this.max);
            numberSlider.setEntryWidth(this.entryWidth);
            numberSlider.setLabel(this.label);
            numberSlider.setCallback(this.callback);
            return numberSlider;
        }

        @Override
        public Builder bounds(Rectangle bounds) {
            this.bounds = bounds;
            return this;
        }

        @Override
        public Builder bounds(int x, int y, int width, int height) {
            this.bounds = new Rectangle(x, y, width, height);
            if (this.entryWidth == -1) this.entryWidth(width);
            return this;
        }

        @Override
        public Builder bounds(Vec2i pos, IntSize size) {
            this.bounds = new Rectangle(pos.x, pos.y, size.width(), size.height());
            if (this.entryWidth == -1) this.entryWidth(size.width());
            return this;
        }

        @Override
        public Builder entryWidth(int width) {
            if (width < 0) throw new IllegalArgumentException("Entry width should be non-negative.");
            this.entryWidth = width;
            return this;
        }

        @Override
        public Builder label(@Nullable String label) {
            return this.label(TextObject.nullToEmpty(label));
        }

        @Override
        public Builder label(TextObject label) {
            this.label = label;
            return this;
        }

        public Builder callback(IntConsumer callback) {
            this.callback = callback;
            return this;
        }

        public Builder value(int value, int min, int max) {
            this.value = value;
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder value(Config.IntEntry entry) {
            return this.value(entry.get(), entry.getMinValue(), entry.getMaxValue());
        }

        @Override
        @Deprecated
        public TextEntry.@Nullable Builder text(String text) {
            return this;
        }

        @Override
        @Deprecated
        public TextEntry.Builder text(Config.StringEntry entry) {
            return this;
        }
    }

    static class ArrowButton extends Button {
        private long previousCommand;
        private long pressedTime;
        private boolean up = false;

        @SuppressWarnings("SameParameterValue")
        protected ArrowButton(int x, int y, int width, int height, Runnable command) {
            super(x, y, width, height);
            this.command = command;
        }

        @Override
        public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
            var bounds = this.getBounds();
            var pressed = this.isPressed();
            var hovered = this.isHovered();

            Color iconColor;
            if (pressed) {
                renderer.fill(bounds, Color.WHITE.withAlpha(0x80));
                renderer.drawEffectBox(bounds, new Insets(2));

                iconColor = Color.WHITE;
            } else if (hovered) {
                renderer.hovered();
                renderer.fill(bounds, Color.WHITE.withAlpha(0x60));
                renderer.drawEffectBox(bounds, new Insets(2));

                iconColor = Color.WHITE;
            } else {
                renderer.fill(bounds, Color.WHITE.withAlpha(0x40));

                iconColor = Color.WHITE.withAlpha(0x80);
            }

            var mx = this.x + this.width / 2;
            var my = this.x + this.height / 2;

            renderer.setLineThickness(3);
            var outer = this.up ? my + 3 : my - 3;
            var inner = this.up ? my - 3 : my + 3;
            renderer.line(mx - 3, inner, mx, outer, Color.WHITE);
            renderer.line(mx, outer, mx + 3, outer, Color.WHITE);
        }

        @Override
        public boolean mousePress(int x, int y, int button) {
            if (!this.isWithinBounds(x, y)) return false;

            this.pressedTime = System.currentTimeMillis();

            return super.mousePress(x, y, button);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.isPressed()) {
                if (this.pressedTime + 1000 < System.currentTimeMillis()) {
                    if (this.previousCommand < System.currentTimeMillis()) {
                        this.previousCommand = System.currentTimeMillis() + 25;
                        this.click();
                    }
                }
            }
        }

        @Override
        protected void click() {
            this.command.run();
        }

        public void setUp(boolean up) {
            this.up = up;
        }

        public boolean isUp() {
            return this.up;
        }
    }
}
