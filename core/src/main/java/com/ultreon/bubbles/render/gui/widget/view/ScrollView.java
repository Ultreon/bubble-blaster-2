package com.ultreon.bubbles.render.gui.widget.view;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ScrollView extends View {
    @SuppressWarnings("FieldCanBeLocal")
    private final Screen screen;
    private Rectangle outerBounds;
    private Rectangle innerBounds;

    private int tickEventCode;
    private int renderEventCode;
    private Set<GuiComponent> children;
    private boolean eventsActive;

    public ScrollView(Screen screen, Rectangle innerBounds, Rectangle outerBounds) {
        super((int) outerBounds.x, (int) outerBounds.y, (int) outerBounds.width, (int) outerBounds.height);
        this.innerBounds = innerBounds;
        this.outerBounds = outerBounds;
        this.screen = screen;
        this.make();
    }

    public void setOuterBounds(Rectangle outerBounds) {
        this.outerBounds = outerBounds;
    }

    public void setInnerBounds(Rectangle innerBounds) {
        this.innerBounds = innerBounds;
    }

    @Override
    public void render(@NotNull Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        renderer.scissored((int) this.outerBounds.getX(), (int) this.outerBounds.getY(), (int) this.outerBounds.getWidth(), (int) this.outerBounds.getHeight(), () -> {
            for (var child : this.children) {
                child.render(renderer, mouseX, mouseY, deltaTime);
            }
        });
    }

    public void add(GuiComponent inputWidget) {
        this.children.add(inputWidget);
    }
}
