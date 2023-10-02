package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.util.helpers.MathHelper;
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
            this.activated = this.getBounds().contains(x, y);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClick(@IntRange(from = 0) int x, @IntRange(from = 0) int y, @IntRange(from = 0) int button, @IntRange(from = 1) int count) {
        if (button == Buttons.LEFT) {
            boolean flag = this.activated = this.isHovered();
            if (flag) {
                return true;
            }
        }

        return super.mouseClick(x, y, button, count);
    }

    @Override
    public boolean keyPress(int keyCode) {
        if (!this.activated) return false;

        if (keyCode == Input.Keys.BACKSPACE) {
            if (this.text.isEmpty()) return false;

            String leftText = this.text.substring(0, this.cursorIndex - 1);
            String rightText = this.text.substring(this.cursorIndex);

            String text = leftText + rightText;
            if (this.responder.test(text)) {
                this.text = text;
                this.layout.setText(this.font, text.substring(0, this.cursorIndex));
                this.cursorIndex = MathHelper.clamp(this.cursorIndex - 1, 0, this.text.length());
            }
            return true;
        }

        if (keyCode == Input.Keys.ESCAPE) {
            if (this.text.isEmpty()) return false;
            if (this.cursorIndex >= this.text.length() - 1) return false;

            String leftText = this.text.substring(0, this.cursorIndex);
            String rightText = this.text.substring(this.cursorIndex + 1);

            String text = leftText + rightText;
            if (this.responder.test(text)) {
                this.text = text;
                this.layout.setText(this.font, text.substring(0, this.cursorIndex));
                this.cursorIndex = MathHelper.clamp(this.cursorIndex - 1, 0, this.text.length());
            }
            return true;
        }

        if (keyCode == Input.Keys.LEFT) {
            this.cursorIndex = MathHelper.clamp(this.cursorIndex - 1, 0, this.text.length());
            return true;
        }

        if (keyCode == Input.Keys.RIGHT) {
            this.cursorIndex = MathHelper.clamp(this.cursorIndex + 1, 0, this.text.length());
            return true;
        }
        return false;
    }

    @Override
    public boolean charType(char character) {
        if ((short) character >= 32) {
            String leftText = this.text.substring(0, this.cursorIndex);
            String rightText = this.text.substring(this.cursorIndex);

            String text = leftText + character + rightText;
            if (this.responder.test(text)) {
                this.text = text;
                this.layout.setText(this.font, text.substring(0, this.cursorIndex));
                this.cursorIndex++;
            }

            return true;
        }

        return false;
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        Rectangle bounds = this.getBounds();
        if (this.activated) {
            renderer.fill(bounds, Color.WHITE.withAlpha(0x60));
            renderer.fillEffect(0, this.height - 4, this.width, 4);
        } else {
            renderer.fill(bounds, Color.WHITE.withAlpha(0x40));
        }

        renderer.drawText(this.font, this.text, 0, 0, Color.WHITE.withAlpha(0xC0));

        int cursorX = this.text.isEmpty() ? 0 : (int) this.layout.width;

        if (this.cursorIndex >= this.text.length()) {
            renderer.fillEffect(cursorX + 2, this.y + 2, 2, this.height - 4);
        } else {
            int width = this.font.getData().getGlyph(this.text.charAt(this.cursorIndex)).width;
            renderer.fillEffect(cursorX, this.height - 2, width, 2);
        }
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    @NotNull
    public String getText() {
        return this.text;
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
            if (this.bounds == null) throw new IllegalArgumentException("Missing bounds for creating OptionsTextEntry.");

            OptionsTextEntry obj = new OptionsTextEntry((int) this.bounds.x, (int) this.bounds.y, (int) this.bounds.width, (int) this.bounds.height);
            obj.setText(this.text == null ? "" : this.text);

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
