package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.border.Border;
import com.ultreon.bubbles.util.helpers.Mth;

import java.awt.*;

@SuppressWarnings("unused")
public class OptionsNumberInput extends OptionsTextEntry {
    private final ArrowButton upButton;
    private final ArrowButton downButton;

    // Value
    private int value;
    private int min;
    private int max;

    public OptionsNumberInput(Rectangle bounds, int value, int min, int max) {
        this(bounds.x, bounds.y, bounds.width, bounds.height, value, min, max);
    }

    public OptionsNumberInput(int x, int y, int width, int height, int value, int min, int max) {
        super(x, y, width, height);
        this.value = value;
        this.min = min;
        this.max = max;
        this.upButton = new ArrowButton(0, 0, 0, 0);
        this.downButton = new ArrowButton(0, 0, 0, 0);

        upButton.setCommand(this::add);
        downButton.setCommand(this::subtract);

        text = Integer.toString(value);

        cursorIndex = Integer.toString(value).length();
    }

    private void add() {
        value = Mth.clamp(value + 1, min, max);
        text = Integer.toString(value);
    }

    private void subtract() {
        value = Mth.clamp(value - 1, min, max);
        text = Integer.toString(value);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (upButton.mousePress(x, y, button)) return true;
        if (downButton.mousePress(x, y, button)) return true;
        super.mousePress(x, y, button);
        if (!activated) {
            try {
                value = Mth.clamp(Integer.parseInt(text), min, max);
                if (!text.equals(Integer.toString(value)))
                    cursorIndex = Integer.toString(value).length();
                text = Integer.toString(value);
                return true;
            } catch (NumberFormatException e) {
                value = Mth.clamp(0, min, max);
                text = Integer.toString(value);
                cursorIndex = text.length();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPress(int keyCode) {
        if (super.keyPress(keyCode)) return true;

        if (keyCode == KeyboardInput.Map.KEY_BACK_SPACE) {
            if (cursorIndex == 0) return false;
            String leftText = text.substring(0, cursorIndex - 1);
            String rightText = text.substring(cursorIndex);

            text = leftText + rightText;

            cursorIndex--;
            cursorIndex = Mth.clamp(cursorIndex, 0, text.length());
            return true;
        }

        if (keyCode == KeyboardInput.Map.KEY_LEFT) {
            cursorIndex--;

            cursorIndex = Mth.clamp(cursorIndex, 0, text.length());
            return true;
        }

        if (keyCode == KeyboardInput.Map.KEY_RIGHT) {
            cursorIndex++;

            cursorIndex = Mth.clamp(cursorIndex, 0, text.length());
            return true;
        }

        return false;
    }

    @Override
    public boolean charType(char character) {
        if ("0123456789".contains(Character.toString(character))) {
//                text += c;
            String leftText = text.substring(0, cursorIndex);
            String rightText = text.substring(cursorIndex);

            text = leftText + character + rightText;

            cursorIndex++;

            cursorIndex = Mth.clamp(cursorIndex, 0, text.length());
            return true;
        }
        return false;
    }

    @Override
    public void make() {
        super.make();

        upButton.make();
        downButton.make();
    }

    @Override
    public void destroy() {
        super.destroy();

        upButton.destroy();
        downButton.destroy();
    }

    @Override
    public boolean mouseClick(int x, int y, int button, int count) {
        if (upButton.mouseClick(x, y, button, count)) return true;
        if (downButton.mouseClick(x, y, button, count)) return true;
        return super.mouseClick(x, y, button, count);
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        if (upButton.mouseRelease(x, y, button)) return true;
        if (downButton.mouseRelease(x, y, button)) return true;
        return super.mouseRelease(x, y, button);
    }

    @Override
    public void render(Renderer renderer) {
        upButton.setY(0);
        upButton.setX(getBounds().width - 24);
        upButton.setHeight(getBounds().height / 2);
        upButton.setWidth(24);
        upButton.setText("+");

        downButton.setY(getBounds().height / 2);
        downButton.setX(getBounds().width - 24);
        downButton.setHeight(getBounds().height / 2);
        downButton.setWidth(24);
        downButton.setText("-");

        if (activated) {
            renderer.setColor(Color.rgb(0x808080));
            fill(renderer, 0, 0, width, height, 0xff808080);

            renderer.drawEffectBox(0, 0, width, height, new Insets(0, 0, 2, 0));
        } else {
            fill(renderer, 0, 0, width, height, 0xff505050);
        }

        renderer.setColor(Color.rgb(0xffffffff));
        font.draw(renderer, text, 24, 2, getHeight() / 2f, Anchor.W);

        int cursorX;
        renderer.setColor(Color.rgb(0xff00c0c0));
        if (cursorIndex >= text.length()) {
            if (text.length() != 0) {
                cursorX = font.width(24, text.substring(0, cursorIndex)) + 2;
            } else {
                cursorX = 0;
            }

            renderer.line(cursorX, 2, cursorX, getHeight() - 2);
            renderer.line(cursorX + 1, 2, cursorX + 1, getHeight() - 2);
        } else {
            if (text.length() != 0) {
                cursorX = font.width(24, text.substring(0, cursorIndex));
            } else {
                cursorX = 0;
            }

            int width = font.width(24, text.charAt(cursorIndex));

            renderer.line(cursorX, getHeight() - 2, cursorX + width, getHeight() - 2);
            renderer.line(cursorX, getHeight() - 1, cursorX + width, getHeight() - 1);
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = Mth.clamp(value, min, max);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        value = Mth.clamp(value, min, max);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        value = Mth.clamp(value, min, max);
    }

    static class ArrowButton extends OptionsButton {
        private long previousCommand;
        private long pressedTime;

        @SuppressWarnings("SameParameterValue")
        protected ArrowButton(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public void render(Renderer renderer) {
            Color textColor;

            Rectangle bounds = getBounds();

            if (isPressed() && isHovered()) {
                // Border
                Paint old = renderer.getPaint();
                GradientPaint p = new GradientPaint(0, y, Color.rgb(0x0080ff).toAwt(), 0f, y + getHeight(), Color.rgb(0x00ff80).toAwt());
                renderer.paint(p);
                renderer.fill(bounds);
                renderer.paint(old);

                textColor = Color.white;
            } else if (isHovered()) {
                renderer.setColor(Color.rgb(0x808080));
                renderer.fill(bounds);

                // Border
                GradientPaint p = new GradientPaint(0, y, Color.rgb(0x0080ff).toAwt(), 0f, y + getHeight(), Color.rgb(0x00ff80).toAwt());
                Border border = new Border(2, 2, 2, 2);
                border.setPaint(p);
                border.paintBorder(renderer, bounds.x, bounds.y, bounds.width, bounds.height);

                textColor = Color.rgb(0xffffff);
            } else {
                renderer.setColor(Color.rgb(0x808080));
                renderer.fill(bounds);

                textColor = Color.rgb(0x808080);
            }

            drawText(renderer, textColor, getSize(), text, font);
        }

        @Override
        public boolean mousePress(int x, int y, int button) {
            pressedTime = System.currentTimeMillis();

            return super.mousePress(x, y, button);
        }

        @Override
        public void tick() {
            super.tick();
            if (isPressed()) {
                if (pressedTime + 1000 < System.currentTimeMillis()) {
                    if (previousCommand < System.currentTimeMillis()) {
                        previousCommand = System.currentTimeMillis() + 25;
                        this.click();
                    }
                }
            }
        }
    }
}