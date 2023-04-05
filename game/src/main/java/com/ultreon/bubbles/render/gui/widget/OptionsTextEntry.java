package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.api.event.keyboard.KeyboardModifiers;
import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.event.v1.type.KeyEventType;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.bubbles.vector.Vec2i;
import com.ultreon.bubbles.vector.size.IntSize;
import org.checkerframework.checker.builder.qual.ReturnsReceiver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OptionsTextEntry extends GuiComponent {
    // Fonts.
    protected final Font defaultFont = new Font(Util.getGame().getSansFontName(), Font.PLAIN, 24);

    // Cursor Index/
    protected int cursorIndex;

    // Values.
    protected String text;

    // State
    protected boolean activated;
    private Predicate<String> responder = text -> true;

    @Deprecated(since = "0.0.3071-indev5", forRemoval = true)
    public OptionsTextEntry(@NotNull Rectangle bounds) {
        this(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public OptionsTextEntry(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public boolean mouseRelease(@IntRange(from = 0) int x, @IntRange(from = 0) int y, @IntRange(from = 1) int button) {
        if (button == 1) {
            activated = getBounds().contains(x, y);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClick(@IntRange(from = 0) int x, @IntRange(from = 0) int y, @IntRange(from = 1) int button, @IntRange(from = 1) int count) {
        if (button == 1) {
            boolean flag = activated = isWithinBounds(x, y);
            if (flag) {
                return true;
            }
        }

        return super.mouseClick(x, y, button, count);
    }

    @Override
    public boolean keyPress(int keyCode, char character) {
        if (!activated) return false;

        if (keyCode == KeyboardInput.Map.KEY_BACK_SPACE) {
            if (text.length() == 0) return false;

            String leftText = text.substring(0, cursorIndex - 1);
            String rightText = text.substring(cursorIndex);

            String text = leftText + rightText;
            if (responder.test(text)) {
                this.text = text;
                cursorIndex = Mth.clamp(cursorIndex - 1, 0, this.text.length());
            }
            return true;
        }

        if (keyCode == KeyboardInput.Map.KEY_ESCAPE) {
            if (text.length() == 0) return false;
            if (cursorIndex >= text.length() - 1) return false;

            String leftText = text.substring(0, cursorIndex);
            String rightText = text.substring(cursorIndex + 1);

            String text = leftText + rightText;
            if (responder.test(text)) {
                this.text = text;
                cursorIndex = Mth.clamp(cursorIndex - 1, 0, this.text.length());
            }
            return true;
        }

        if (keyCode == KeyboardInput.Map.KEY_LEFT) {
            cursorIndex = Mth.clamp(cursorIndex - 1, 0, this.text.length());
            return true;
        }

        if (keyCode == KeyboardInput.Map.KEY_RIGHT) {
            cursorIndex = Mth.clamp(cursorIndex + 1, 0, this.text.length());
            return true;
        }

        char c = character;

        if (keyCode == KeyboardInput.Map.KEY_DEAD_ACUTE) {
            c = '\'';
        }

        if (keyCode == KeyboardInput.Map.KEY_QUOTEDBL) {
            c = '"';
        }

        if ((short) c >= 32) {
            String leftText = this.text.substring(0, cursorIndex);
            String rightText = this.text.substring(cursorIndex);

            String text = leftText + c + rightText;
            if (responder.test(text)) {
                this.text = text;
                cursorIndex++;
            }

            return true;
        }

        return false;
    }

    @Deprecated(forRemoval = true, since = "0.0.3047-indev5")
    public void onKeyboard(KeyEventType type, char key, int keyCode, KeyboardModifiers modifiers) {
        if (!activated) return;

        if (type == KeyEventType.PRESS || type == KeyEventType.HOLD) {
            if (keyCode == KeyboardInput.Map.KEY_BACK_SPACE) {
                if (text.length() == 0) return;

                String leftText = text.substring(0, cursorIndex - 1);
                String rightText = text.substring(cursorIndex);

                text = leftText + rightText;

                cursorIndex = Mth.clamp(cursorIndex - 1, 0, text.length());
                return;
            }

            if (keyCode == KeyboardInput.Map.KEY_LEFT) {
                cursorIndex = Mth.clamp(cursorIndex - 1, 0, text.length());
                return;
            }

            if (keyCode == KeyboardInput.Map.KEY_RIGHT) {
                cursorIndex = Mth.clamp(cursorIndex + 1, 0, text.length());
                return;
            }

            char c = key;

            if (keyCode == KeyboardInput.Map.KEY_DEAD_ACUTE) {
                c = '\'';
            }

            if (keyCode == KeyboardInput.Map.KEY_QUOTEDBL) {
                c = '"';
            }

            if ((short) c >= 32) {
//                text += c;
                String leftText = text.substring(0, cursorIndex);
                String rightText = text.substring(cursorIndex);

                text = leftText + c + rightText;

                cursorIndex++;
            }
        }
    }

    @Override
    public void render(Renderer renderer) {
        if (activated) {
            renderer.color(Color.rgb(0x808080));
            renderer.fill(getBounds());

            Paint old = renderer.getPaint();
            GradientPaint p = new GradientPaint(0, this.y, Color.rgb(0x00c0ff).toAwt(), 0f, this.y + getHeight(), Color.rgb(0x00ffc0).toAwt());
            renderer.paint(p);
            renderer.fill(new Rectangle(x, y + height, width, 4));
            renderer.paint(old);
        } else {
            renderer.color(Color.rgb(0x404040));
            renderer.fill(getBounds());
        }

        renderer.color(Color.rgb(0xffffffff));
        GraphicsUtils.drawLeftAnchoredString(renderer, text, new Vec2i(2, getY() + getHeight() - (getHeight() - 4)), getHeight() - 4, defaultFont);

        FontMetrics fontMetrics = renderer.fontMetrics(defaultFont);

        int cursorX;
        renderer.color(Color.rgb(0xff00c0c0));
        if (cursorIndex >= text.length()) {
            if (text.length() != 0) {
                cursorX = fontMetrics.stringWidth(text.substring(0, cursorIndex)) + 2 + getX();
            } else {
                cursorX = getX();
            }

            cursorX += getX();

            renderer.line(cursorX, getY() + 2, cursorX, getY() + getHeight() - 2);
            renderer.line(cursorX + 1, getY() + 2, cursorX + 1, getY() + getHeight() - 2);
        } else {
            if (text.length() != 0) {
                cursorX = fontMetrics.stringWidth(text.substring(0, cursorIndex)) + getX();
            } else {
                cursorX = getX();
            }

            int width = fontMetrics.charWidth(text.charAt(cursorIndex));

            renderer.line(cursorX, getY() + getHeight() - 2, cursorX + width, getY() + getHeight() - 2);
            renderer.line(cursorX, getY() + getHeight() - 1, cursorX + width, getY() + getHeight() - 1);
        }
    }

    public void setText(@Nullable String text) {
        this.text = text == null ? "{null}" : text;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setResponder(@NonNull Consumer<@NonNull String> responder) {
        this.responder = (@NonNull String text) -> {
            responder.accept(text);
            return true;
        };
    }

    public void setResponder(@NonNull Predicate<@NonNull String> responder) {
        this.responder = responder;
    }

    public static class Builder {
        public Rectangle bounds = null;
        @Nullable
        private String text = "";

        public Builder() {

        }

        public OptionsTextEntry build() {
            if (bounds == null) throw new IllegalArgumentException("Missing bounds for creating OptionsTextEntry.");

            OptionsTextEntry obj = new OptionsTextEntry(bounds.x, bounds.y, bounds.width, bounds.height);
            obj.setText(text);

            return obj;
        }

        public Builder bounds(Rectangle _bounds) {
            this.bounds = _bounds;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            this.bounds = new Rectangle(x, y, width, height);
            return this;
        }

        public Builder bounds(Vec2i pos, IntSize size) {
            this.bounds = new Rectangle(pos.x, pos.y, size.width(), size.height());
            return this;
        }

        @Nullable
        @ReturnsReceiver
        public Builder text(String _text) {
            this.text = _text;
            return this;
        }
    }
}
