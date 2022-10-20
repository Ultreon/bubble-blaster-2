package com.ultreon.bubbles.event.v1;

import com.ultreon.bubbles.render.TextureCollection;

@Deprecated
public class CollectTexturesEvent extends Event {
    private final TextureCollection textureCollection;

    public CollectTexturesEvent(TextureCollection textureCollection) {
        this.textureCollection = textureCollection;
    }

    public TextureCollection getTextureCollection() {
        return textureCollection;
    }
}
