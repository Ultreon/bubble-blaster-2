package com.ultreon.bubbles.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.ultreon.bubbles.BubbleBlaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class TextureCollection {
    private static final Logger LOGGER = LogManager.getLogger("Texture-Collection");
    private final HashMap<Index, Texture> textures = new HashMap<>();
    private final BubbleBlaster game = new BubbleBlaster();

    public TextureCollection() {

    }

    public void set(Index index, ITexture texture) {
        if (textures.containsKey(index)) {
            LOGGER.warn("Texture override: " + index);
        }

        if (BubbleBlaster.isOnRenderingThread()) {
            var renderer = this.game.getRenderer();
            var fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            fbo.begin();
            texture.render(renderer);
            fbo.end();
            this.textures.put(index, fbo.getColorBufferTexture());
        } else {
            Texture tex = BubbleBlaster.invokeAndWait(() -> {
                var renderer = this.game.getRenderer();
                var fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
                fbo.begin();
                texture.render(renderer);
                fbo.end();
                return fbo.getColorBufferTexture();
            });
            this.textures.put(index, tex);
        }
    }

    public Texture get(Index location) {
        return textures.get(location);
    }

    public String toString() {
        return "TextureCollection[" + textures.size() + " textures]";
    }

    public record Index(String modId, String id) {
        @Override
        public String toString() {
            return modId + "#" + id;
        }
    }
}
