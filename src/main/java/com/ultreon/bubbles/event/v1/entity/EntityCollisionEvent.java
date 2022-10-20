package com.ultreon.bubbles.event.v1.entity;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.event.v1.CollisionEvent;
import com.ultreon.bubbles.render.Renderer;

/**
 * Render Event
 * This event is for rendering the game objects on the canvas.
 *
 * @see Renderer
 * @see Renderer
 */
@Deprecated
public class EntityCollisionEvent extends CollisionEvent {
    private final BubbleBlaster main;
    private final double deltaTime;
    private final Entity source;
    private final Entity target;

    public EntityCollisionEvent(BubbleBlaster main, double deltaTime, Entity source, Entity target) {
        super(main, source, target);
        this.deltaTime = deltaTime;
        this.source = source;
        this.target = target;
        this.main = main;
    }

    public BubbleBlaster getMain() {
        return main;
    }

    public Entity getSource() {
        return source;
    }

    public Entity getTarget() {
        return target;
    }

    public double getDeltaTime() {
        return deltaTime;
    }
}
