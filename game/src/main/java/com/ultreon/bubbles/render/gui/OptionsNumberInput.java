package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.gui.Rectangle;
import com.ultreon.bubbles.render.screen.gui.border.Border;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.helpers.MathHelper;

import java.awt.*;
import java.awt.geom.Point2D;

@SuppressWarnings("unused")
public class OptionsNumberInput extends OptionsTextEntry {
    private final ArrowButton upButton;
    private final ArrowButton downButton;
    private boolean eventsActive = false;

    // Value
    private int value;
    private int min;
    private int max;

    public OptionsNumberInput(com.ultreon.bubbles.render.screen.gui.Rectangle bounds, int value, int min, int max) {
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
        value = MathHelper.clamp(value + 1, min, max);
        text = Integer.toString(value);
    }

    private void subtract() {
        value = MathHelper.clamp(value - 1, min, max);
        text = Integer.toString(value);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (upButton.mousePress(x, y, button)) return true;
        if (downButton.mousePress(x, y, button)) return true;
        super.mousePress(x, y, button);
        if (!activated) {
            try {
                value = MathHelper.clamp(Integer.parseInt(text), min, max);
                if (!text.equals(Integer.toString(value)))
                    cursorIndex = Integer.toString(value).length();
                text = Integer.toString(value);
                return true;
            } catch (NumberFormatException e) {
                value = MathHelper.clamp(0, min, max);
                text = Integer.toString(value);
                cursorIndex = text.length();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPress(int keyCode, char character) {
        if (super.keyPress(keyCode, character)) return true;

        if (keyCode == KeyboardInput.Map.KEY_BACK_SPACE) {
            if (cursorIndex == 0) return false;
            String leftText = text.substring(0, cursorIndex - 1);
            String rightText = text.substring(cursorIndex);

            text = leftText + rightText;

            cursorIndex--;
            cursorIndex = MathHelper.clamp(cursorIndex, 0, text.length());
            return true;
        }

        if (keyCode == KeyboardInput.Map.KEY_LEFT) {
            cursorIndex--;

            cursorIndex = MathHelper.clamp(cursorIndex, 0, text.length());
            return true;
        }

        if (keyCode == KeyboardInput.Map.KEY_RIGHT) {
            cursorIndex++;

            cursorIndex = MathHelper.clamp(cursorIndex, 0, text.length());
            return true;
        }

        char c = character;

        if (keyCode == KeyboardInput.Map.KEY_DEAD_ACUTE) {
            c = '\'';
        }

        if (keyCode == KeyboardInput.Map.KEY_QUOTEDBL) {
            c = '"';
        }

        if ("0123456789".contains(Character.toString(c))) {
//                text += c;
            String leftText = text.substring(0, cursorIndex);
            String rightText = text.substring(cursorIndex);

            text = leftText + c + rightText;

            cursorIndex++;

            cursorIndex = MathHelper.clamp(cursorIndex, 0, text.length());
        }
        return true;
    }

    @Override
    public void make() {
        eventsActive = true;
        upButton.make();
        downButton.make();
    }

    @Override
    public void destroy() {
        eventsActive = false;
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
    public boolean isValid() {
        return eventsActive;
    }

    @Override
    public void render(Renderer renderer) {
        upButton.setY(this.y);
        upButton.setX(this.x + getBounds().width - 24);
        upButton.setHeight(getBounds().height / 2);
        upButton.setWidth(24);
        upButton.setText("+");

        downButton.setY(this.y + getBounds().height / 2);
        downButton.setX(this.x + getBounds().width - 24);
        downButton.setHeight(getBounds().height / 2);
        downButton.setWidth(24);
        downButton.setText("-");

        if (activated) {
            renderer.color(new Color(128, 128, 128));
            renderer.fill(getBounds());

            Paint old = renderer.getPaint();
            GradientPaint p = new GradientPaint(this.x, 0, new Color(0, 192, 255), this.x + getWidth(), 0, new Color(0, 255, 192));
            renderer.paint(p);
            renderer.fill(new com.ultreon.bubbles.render.screen.gui.Rectangle(x, y + height - 2, width, 2));
            renderer.paint(old);
        } else {
            renderer.color(new Color(79, 79, 79));
            renderer.fill(getBounds());
        }

        Renderer gg1 = renderer.subInstance(x, y, width, height);
        gg1.color(new Color(255, 255, 255, 255));

        cursorIndex = MathHelper.clamp(cursorIndex, 0, text.length());
        GraphicsUtils.drawLeftAnchoredString(gg1, text, new Point2D.Double(8, height - (height - 5)), height - 5, defaultFont);

        FontMetrics fontMetrics = renderer.fontMetrics(defaultFont);

        upButton.render(renderer);
        downButton.render(renderer);

        int cursorX;
        gg1.color(new Color(0, 192, 192, 255));
        if (cursorIndex >= text.length()) {
            if (text.length() != 0) {
                cursorX = fontMetrics.stringWidth(text.substring(0, cursorIndex)) + 8;
            } else {
                cursorX = 10;
            }

            gg1.line(cursorX, 2, cursorX, height - 5);
            gg1.line(cursorX + 1, 2, cursorX + 1, height - 5);
        } else {
            if (text.length() != 0) {
                cursorX = fontMetrics.stringWidth(text.substring(0, cursorIndex)) + 8;
            } else {
                cursorX = 10;
            }

            int width = fontMetrics.charWidth(text.charAt(cursorIndex));

            gg1.line(cursorX, height - 5, cursorX + width, height - 5);
            gg1.line(cursorX, height - 4, cursorX + width, height - 4);
        }

        gg1.dispose();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = MathHelper.clamp(value, min, max);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        value = MathHelper.clamp(value, min, max);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        value = MathHelper.clamp(value, min, max);
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
                GradientPaint p = new GradientPaint(0, y, new Color(0, 192, 255), 0f, y + getHeight(), new Color(0, 255, 192));
                renderer.paint(p);
                renderer.fill(bounds);
                renderer.paint(old);

                textColor = Color.white;
            } else if (isHovered()) {
                renderer.color(new Color(128, 128, 128));
                renderer.fill(bounds);

                // Border
                GradientPaint p = new GradientPaint(0, y, new Color(0, 192, 255), 0f, y + getHeight(), new Color(0, 255, 192));
                Border border = new Border(2, 2, 2, 2);
                border.setPaint(p);
                border.paintBorder(renderer, bounds.x, bounds.y, bounds.width, bounds.height);

                textColor = new Color(255, 255, 255);
            } else {
                renderer.color(new Color(128, 128, 128));
                renderer.fill(bounds);

                textColor = new Color(192, 192, 192);
            }

            paint0a(renderer, textColor, getBounds(), text);
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
