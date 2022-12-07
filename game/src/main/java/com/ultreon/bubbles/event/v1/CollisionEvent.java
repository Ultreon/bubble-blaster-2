package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.GameObject;
import com.ultreon.commons.lang.ICancellable;

@Deprecated
public class CollisionEvent extends Event implements ICancellable {
    private final BubbleBlaster game;
    private final GameObject source;
    private final GameObject target;

    public CollisionEvent(BubbleBlaster game, GameObject source, GameObject target) {
        this.game = game;
        this.source = source;
        this.target = target;
    }

    public BubbleBlaster getGame() {
        return game;
    }

    public GameObject getSource() {
        return source;
    }

    public GameObject getTarget() {
        return target;
    }
}
