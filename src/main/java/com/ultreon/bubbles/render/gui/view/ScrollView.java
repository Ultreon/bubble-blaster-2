package com.ultreon.bubbles.render.gui.view;

import com.ultreon.bubbles.event.v1.SubscribeEvent;
import com.ultreon.bubbles.event.v1.TickEvent;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.Screen;
import com.ultreon.bubbles.render.screen.gui.InputWidget;
import com.ultreon.bubbles.render.screen.gui.Rectangle;
import com.ultreon.bubbles.render.screen.gui.view.View;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class ScrollView extends View {
    @SuppressWarnings("FieldCanBeLocal")
    private final Screen screen;
    private Rectangle outerBounds;
    private Rectangle innerBounds;

    private int tickEventCode;
    private int renderEventCode;
    private Set<InputWidget> children;
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

    @SuppressWarnings("EmptyMethod")
    @SubscribeEvent
    private void tick(TickEvent event) {

    }

    @Override
    public void render(@NonNull Renderer renderer) {
        this.containerGraphics = renderer.subInstance(outerBounds.getX(), outerBounds.getY(), outerBounds.getWidth(), outerBounds.getHeight());
        for (InputWidget child : this.children) {
            child.render(this.containerGraphics);
        }
    }

    public void add(InputWidget inputWidget) {
        this.children.add(inputWidget);
    }

    @Override
    public void make() {
        BubbleBlaster.getEventBus().subscribe(this);
        this.eventsActive = true;
    }

    @Override
    public void destroy() {
        BubbleBlaster.getEventBus().unsubscribe(this);
        this.eventsActive = false;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
