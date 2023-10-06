package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.commons.v0.size.IntSize;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.text.v1.TextObject;
import org.checkerframework.checker.builder.qual.ReturnsReceiver;
import org.checkerframework.com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextEntry extends GuiComponent {
    // Cursor Index/
    protected int cursorIndex;

    // Values.
    protected String text = "";

    protected final GlyphLayout layout = new GlyphLayout();

    // State
    protected boolean activated;
    private Predicate<String> responder = text -> true;
    protected int entryWidth;
    protected TextObject label;

    public TextEntry(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (button == Buttons.LEFT) {
            this.activated = this.isWithinBounds(x, y);
            return true;
        }

        return super.mousePress(x, y, button);
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
                this.layout.setText(this.font, text.substring(0, Mth.clamp(this.cursorIndex, 0, this.text.length() - 1)));
                this.cursorIndex = Mth.clamp(this.cursorIndex - 1, 0, this.text.length());
                this.revalidate();
            }
            return true;
        }

        if (keyCode == Input.Keys.FORWARD_DEL) {
            if (this.text.isEmpty()) return false;
            if (this.cursorIndex >= this.text.length() - 1) return false;

            String leftText = this.text.substring(0, this.cursorIndex);
            String rightText = this.text.substring(this.cursorIndex + 1);

            String text = leftText + rightText;
            if (this.responder.test(text)) {
                this.text = text;
                this.layout.setText(this.font, text.substring(0, this.cursorIndex));
                this.cursorIndex = Mth.clamp(this.cursorIndex - 1, 0, this.text.length());
                this.revalidate();
            }
            return true;
        }

        if (keyCode == Input.Keys.LEFT) {
            this.cursorIndex = Mth.clamp(this.cursorIndex - 1, 0, this.text.length());
            this.revalidate();
            return true;
        }

        if (keyCode == Input.Keys.RIGHT) {
            this.cursorIndex = Mth.clamp(this.cursorIndex + 1, 0, this.text.length());
            this.revalidate();
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
                this.revalidate();
            }

            return true;
        }

        return false;
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();

        Gdx.input.setOnscreenKeyboardVisible(false);
    }

    @Override
    public void onFocusGained() {
        super.onFocusGained();

        Gdx.input.setOnscreenKeyboardVisible(true, this.getOnscreenKeyboardType());
    }

    public Input.OnscreenKeyboardType getOnscreenKeyboardType() {
        return Input.OnscreenKeyboardType.Default;
    }

    protected void revalidate() {
        this.layout.setText(this.font, this.text.substring(0, Mth.clamp(this.cursorIndex, 0, this.text.length())));
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        final int entryX = Math.max(this.x + this.width - this.entryWidth, this.x);
        final int entryW = Math.min(this.entryWidth, this.width);
        final int labelX = this.x;
        final int labelW = Math.max(this.width - entryW, 0);

        this.drawBackground(renderer, labelX, labelW, entryX, entryW);
        this.drawText(renderer, entryX, entryW, labelX, labelW);
    }

    protected void drawBackground(Renderer renderer, int labelX, int labelW, int entryX, int entryW) {
        if (this.isFocused()) {
            renderer.fill(labelX, this.y, labelW, this.height, Color.WHITE.withAlpha(0x40));
            renderer.fill(entryX, this.y, entryW, this.height, Color.WHITE.withAlpha(0x80));
            renderer.drawEffectBox(this.x, this.y, this.width, this.height, new Insets(0, 0, 4, 0));
        } else {
            renderer.fill(labelX, this.y, labelW, this.height, Color.WHITE.withAlpha(0x20));
            renderer.fill(entryX, this.y, entryW, this.height, Color.WHITE.withAlpha(0x60));
        }
    }

    protected void drawText(Renderer renderer, int entryX, int entryW, int labelX, int labelW) {
        renderer.scissored(entryX + 2, this.y, entryW - 4, this.height, () -> {
            renderer.drawTextLeft(this.font, this.text, entryX + 10, this.y + this.getHeight() / 2f - 2f, Color.WHITE);

            if (this.isFocused()) {
                float cursorX = this.text.isEmpty() ? entryX + 10 : entryX + 10 + this.layout.width;
                renderer.fillEffect(cursorX, this.y + this.getHeight() / 2f - this.font.getLineHeight() / 2, 2, this.font.getLineHeight());
            }
        });

        renderer.scissored(labelX + 2, this.y, labelW - 4, this.height, () -> {
            renderer.drawTextCenter(this.font, this.label, labelX + labelW / 2f , this.y + this.getHeight() / 2f - 2f, Color.WHITE);
        });
    }

    public void setText(@NotNull String text) {
        this.text = text;
        this.cursorIndex = this.text.length();
        this.revalidate();
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

    public void setEntryWidth(int entryWidth) {
        this.entryWidth = entryWidth;
    }

    public int getEntryWidth() {
        return this.entryWidth;
    }

    public void setLabel(TextObject label) {
        this.label = label;
    }

    public TextObject getLabel() {
        return this.label;
    }

    public static class Builder {
        public Rectangle bounds = null;
        @Nullable
        private String text = "";
        private int entryWidth = -1;
        private TextObject label = TextObject.EMPTY;

        public Builder() {

        }

        public TextEntry build() {
            if (this.bounds == null) throw new IllegalArgumentException("Missing bounds for creating OptionsTextEntry.");
            if (this.entryWidth == -1) throw new IllegalArgumentException("Missing entry width for creating number slider.");

            TextEntry entry = new TextEntry((int) this.bounds.x, (int) this.bounds.y, (int) this.bounds.width, (int) this.bounds.height);
            entry.setText(this.text == null ? "" : this.text);
            entry.setEntryWidth(this.entryWidth);
            entry.setLabel(this.label);

            return entry;
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

    }
}
