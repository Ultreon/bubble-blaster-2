package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.game.BubbleBlaster;

@Deprecated
public class GameExitEvent extends Event {
    private final BubbleBlaster game;

    public GameExitEvent(BubbleBlaster game) {
        this.game = game;
    }

    public BubbleBlaster getGame() {
        return game;
    }
}
