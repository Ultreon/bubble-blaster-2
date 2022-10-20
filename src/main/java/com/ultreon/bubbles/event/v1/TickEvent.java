package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.game.BubbleBlaster;

/**
 * Update Event
 * This event is for updating values, or doing things such as collision.
 *
 * @see Event
 * @see RenderEvent
 */
@Deprecated
public class TickEvent extends Event {
    private final BubbleBlaster main;

    public TickEvent(BubbleBlaster main) {
        this.main = main;
    }

    public BubbleBlaster getGame() {
        return main;
    }
}
