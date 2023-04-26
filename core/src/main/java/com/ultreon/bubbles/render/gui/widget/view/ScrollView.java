package com.ultreon.bubbles.render.gui.widget.view;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.render.gui.widget.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
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
        super(outerBounds.x, outerBounds.y, outerBounds.width, outerBounds.height);
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
    public void render(@NotNull Renderer renderer) {
        renderer.subInstance(outerBounds.getX(), outerBounds.getY(), outerBounds.getWidth(), outerBounds.getHeight(), containerGraphics -> {
            for (GuiComponent child : this.children) {
                child.render(containerGraphics);
            }
        });
    }

    public void add(GuiComponent inputWidget) {
        this.children.add(inputWidget);
    }
}
