package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.helpers.Mth;


@SuppressWarnings("unused")
public class OptionsNumberInput extends OptionsTextEntry {
    private final ArrowButton upButton;
    private final ArrowButton downButton;

    // Value
    private int value;
    private int min;
    private int max;

    public OptionsNumberInput(Rectangle bounds, int value, int min, int max) {
        this((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height, value, min, max);
    }

    public OptionsNumberInput(int x, int y, int width, int height, int value, int min, int max) {
        super(x, y, width, height);
        this.value = value;
        this.min = min;
        this.max = max;
        this.upButton = new ArrowButton(0, 0, 0, 0);
        this.downButton = new ArrowButton(0, 0, 0, 0);

        this.upButton.setCommand(this::add);
        this.downButton.setCommand(this::subtract);

        this.text = Integer.toString(value);

        this.cursorIndex = Integer.toString(value).length();
    }

    private void add() {
        this.value = Mth.clamp(this.value + 1, this.min, this.max);
        this.text = Integer.toString(this.value);
    }

    private void subtract() {
        this.value = Mth.clamp(this.value - 1, this.min, this.max);
        this.text = Integer.toString(this.value);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (this.upButton.mousePress(x, y, button)) return true;
        if (this.downButton.mousePress(x, y, button)) return true;
        super.mousePress(x, y, button);
        if (!this.activated) {
            try {
                this.value = Mth.clamp(Integer.parseInt(this.text), this.min, this.max);
                if (!this.text.equals(Integer.toString(this.value)))
                    this.cursorIndex = Integer.toString(this.value).length();
                this.text = Integer.toString(this.value);
                return true;
            } catch (NumberFormatException e) {
                this.value = Mth.clamp(0, this.min, this.max);
                this.text = Integer.toString(this.value);
                this.cursorIndex = this.text.length();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPress(int keyCode) {
        if (super.keyPress(keyCode)) return true;

        if (keyCode == Input.Keys.BACKSPACE) {
            if (this.cursorIndex == 0) return false;
            String leftText = this.text.substring(0, this.cursorIndex - 1);
            String rightText = this.text.substring(this.cursorIndex);

            this.text = leftText + rightText;
            this.layout.setText(this.font, this.text.substring(0, this.cursorIndex));

            this.cursorIndex--;
            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            return true;
        }

        if (keyCode == Input.Keys.LEFT) {
            this.cursorIndex--;

            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            return true;
        }

        if (keyCode == Input.Keys.RIGHT) {
            this.cursorIndex++;

            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            return true;
        }

        return false;
    }

    @Override
    public boolean charType(char character) {
        if ("0123456789".contains(Character.toString(character))) {
//                text += c;
            String leftText = this.text.substring(0, this.cursorIndex);
            String rightText = this.text.substring(this.cursorIndex);

            this.text = leftText + character + rightText;
            this.layout.setText(this.font, this.text.substring(0, this.cursorIndex));

            this.cursorIndex++;

            this.cursorIndex = Mth.clamp(this.cursorIndex, 0, this.text.length());
            return true;
        }
        return false;
    }

    @Override
    public void make() {
        super.make();

        this.upButton.make();
        this.downButton.make();
    }

    @Override
    public void destroy() {
        super.destroy();

        this.upButton.destroy();
        this.downButton.destroy();
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
        this.upButton.setY(0);
        this.upButton.setX((int) (this.getBounds().width - 24));
        this.upButton.setHeight((int) (this.getBounds().height / 2));
        this.upButton.setWidth(24);
        this.upButton.setText("+");

        this.downButton.setY((int) (this.getBounds().height / 2));
        this.downButton.setX((int) (this.getBounds().width - 24));
        this.downButton.setHeight((int) (this.getBounds().height / 2));
        this.downButton.setWidth(24);
        this.downButton.setText("-");

        if (this.activated) {
            renderer.setColor(Color.rgb(0x808080));
            renderer.fill(0, 0, this.width, this.height, Color.grayscale(0x80));

            renderer.drawEffectBox(0, 0, this.width, this.height, new Insets(0, 0, 2, 0));
        } else {
            renderer.fill(0, 0, this.width, this.height, Color.grayscale(0x50));
        }

        renderer.drawLeftAnchoredText(this.font, this.text, 2, this.getHeight() / 2f, Color.WHITE);

        float cursorX = this.text.isEmpty() ? 0 : this.layout.width;

        renderer.setColor(Color.rgb(0xff00c0c0));
        if (this.cursorIndex >= this.text.length()) {
            if (!this.text.isEmpty()) cursorX = this.layout.width + 2;
            else cursorX = 0;

            renderer.fillEffect(cursorX + 2, this.x + 2, this.y + 2, this.height - 4);
        } else {
            int width = this.font.getData().getGlyph(this.text.charAt(this.cursorIndex)).width;
            renderer.fillEffect(cursorX, this.height - 2, width, 2);
        }
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = Mth.clamp(value, this.min, this.max);
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
        this.value = Mth.clamp(this.value, min, this.max);
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
        this.value = Mth.clamp(this.value, this.min, max);
    }

    static class ArrowButton extends OptionsButton {
        private long previousCommand;
        private long pressedTime;

        @SuppressWarnings("SameParameterValue")
        protected ArrowButton(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
            Rectangle bounds = this.getBounds();
            boolean pressed = this.isPressed();
            boolean hovered = this.isHovered();

            Color textColor;
            if (pressed) {
                renderer.fill(bounds, Color.WHITE.withAlpha(0x40));
                renderer.drawEffectBox(bounds, new Insets(2));

                textColor = Color.WHITE;
            } else if (hovered) {
                renderer.fill(bounds, Color.WHITE.withAlpha(0x40));
                renderer.drawEffectBox(bounds, new Insets(2));

                textColor = Color.WHITE;
            } else {
                renderer.fill(bounds, Color.WHITE.withAlpha(0x40));

                textColor = Color.WHITE.withAlpha(0x80);
            }

            OptionsButton.drawText(renderer, textColor, this.getPos(), this.getSize(), this.text, this.font);
        }

        @Override
        public boolean mousePress(int x, int y, int button) {
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
    }
}
