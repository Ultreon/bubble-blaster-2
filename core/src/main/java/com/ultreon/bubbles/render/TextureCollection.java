package com.ultreon.bubbles.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.GamePlatform;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Objects;

public class TextureCollection {
    private static final Logger LOGGER = GamePlatform.get().getLogger("Texture-Collection");
    private final HashMap<Index, Texture> textures = new HashMap<>();
    private final BubbleBlaster game = BubbleBlaster.getInstance();

    public TextureCollection() {

    }

    public void set(Index index, ITexture texture) {
        if (this.textures.containsKey(index)) {
            LOGGER.warn("Texture override: " + index);
        }

        if (BubbleBlaster.isOnRenderingThread()) {
            Renderer renderer = this.game.getRenderer();
            FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            fbo.begin();
            texture.render(renderer);
            fbo.end();
            this.textures.put(index, fbo.getColorBufferTexture());
        } else {
            Texture tex = BubbleBlaster.invokeAndWait(() -> {
                Renderer renderer = this.game.getRenderer();
                FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
                fbo.begin();
                texture.render(renderer);
                fbo.end();
                return fbo.getColorBufferTexture();
            });
            this.textures.put(index, tex);
        }
    }

    public Texture get(Index location) {
        return this.textures.get(location);
    }

    public String toString() {
        return "TextureCollection[" + this.textures.size() + " textures]";
    }

    public static final class Index {
        private final String modId;
        private final String id;

        public Index(String modId, String id) {
            this.modId = modId;
            this.id = id;
        }

        @Override
            public String toString() {
                return this.modId + "#" + this.id;
            }

        public String modId() {
            return this.modId;
        }

        public String id() {
            return this.id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Index that = (Index) obj;
            return Objects.equals(this.modId, that.modId) &&
                    Objects.equals(this.id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.modId, this.id);
        }

        }
}
