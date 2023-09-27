package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.libs.commons.v0.size.IntSize;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import org.checkerframework.checker.builder.qual.ReturnsReceiver;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OptionsTextEntry extends GuiComponent {
    // Cursor Index/
    protected int cursorIndex;

    // Values.
    protected String text;

    protected GlyphLayout layout = new GlyphLayout();

    // State
    protected boolean activated;
    private Predicate<String> responder = text -> true;

    public OptionsTextEntry(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public boolean mouseRelease(@IntRange(from = 0) int x, @IntRange(from = 0) int y, @IntRange(from = 0) int button) {
        if (button == Buttons.LEFT) {
            activated = getBounds().contains(x, y);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClick(@IntRange(from = 0) int x, @IntRange(from = 0) int y, @IntRange(from = 0) int button, @IntRange(from = 1) int count) {
        if (button == Buttons.LEFT) {
            boolean flag = activated = isHovered();
            if (flag) {
                return true;
            }
        }

        return super.mouseClick(x, y, button, count);
    }

    @Override
    public boolean keyPress(int keyCode) {
        if (!activated) return false;

        if (keyCode == Input.Keys.BACKSPACE) {
            if (text.isEmpty()) return false;

            String leftText = text.substring(0, cursorIndex - 1);
            String rightText = text.substring(cursorIndex);

            String text = leftText + rightText;
            if (responder.test(text)) {
                this.text = text;
                layout.setText(font, text.substring(0, cursorIndex));
                cursorIndex = Mth.clamp(cursorIndex - 1, 0, this.text.length());
            }
            return true;
        }

        if (keyCode == Input.Keys.ESCAPE) {
            if (text.isEmpty()) return false;
            if (cursorIndex >= text.length() - 1) return false;

            String leftText = text.substring(0, cursorIndex);
            String rightText = text.substring(cursorIndex + 1);

            String text = leftText + rightText;
            if (responder.test(text)) {
                this.text = text;
                layout.setText(font, text.substring(0, cursorIndex));
                cursorIndex = Mth.clamp(cursorIndex - 1, 0, this.text.length());
            }
            return true;
        }

        if (keyCode == Input.Keys.LEFT) {
            cursorIndex = Mth.clamp(cursorIndex - 1, 0, this.text.length());
            return true;
        }

        if (keyCode == Input.Keys.RIGHT) {
            cursorIndex = Mth.clamp(cursorIndex + 1, 0, this.text.length());
            return true;
        }
        return false;
    }

    @Override
    public boolean charType(char character) {
        if ((short) character >= 32) {
            String leftText = this.text.substring(0, cursorIndex);
            String rightText = this.text.substring(cursorIndex);

            String text = leftText + character + rightText;
            if (responder.test(text)) {
                this.text = text;
                layout.setText(font, text.substring(0, cursorIndex));
                cursorIndex++;
            }

            return true;
        }

        return false;
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        if (activated) {
            fill(renderer, 0, 0, width, height, 0xff808080);

//            Paint old = renderer.getPaint();
//            GradientPaint p = new GradientPaint(0, 0, Color.rgb(0x00c0ff).toAwt(), 0f, getHeight(), Color.rgb(0x00ffc0).toAwt());
//            renderer.paint(p);
//            renderer.fill(new Rectangle(0, height, width, 4));
//            renderer.paint(old);
        } else {
            fill(renderer, 0, 0, width, height, 0xff404040);
        }

        renderer.setColor(Color.rgb(0xffffffff));
        renderer.drawText(text, 0, 0);

        int cursorX;
        renderer.setColor(Color.rgb(0xff00c0c0));
        if (cursorIndex >= text.length()) {
            if (!text.isEmpty()) {
                cursorX = (int) (layout.width + 2);
            } else {
                cursorX = 0;
            }

            renderer.line(cursorX, 2, cursorX, getHeight() - 2);
            renderer.line(cursorX + 1, 2, cursorX + 1, getHeight() - 2);
        } else {
            if (!text.isEmpty()) {
                cursorX = (int) layout.width;
            } else {
                cursorX = 0;
            }

            int width = font.getData().getGlyph(text.charAt(cursorIndex)).width;

            renderer.line(cursorX, getHeight() - 2, cursorX + width, getHeight() - 2);
            renderer.line(cursorX, getHeight() - 1, cursorX + width, getHeight() - 1);
        }
    }

    public void setText(@Nullable String text) {
        this.text = text == null ? "{null}" : text;
    }

    @NotNull
    public String getText() {
        return text;
    }

    public void setResponder(@NotNull Consumer<@NotNull String> responder) {
        this.responder = (@NotNull String text) -> {
            responder.accept(text);
            return true;
        };
    }

    public void setResponder(@NotNull Predicate<@NotNull String> responder) {
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

            OptionsTextEntry obj = new OptionsTextEntry((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
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
