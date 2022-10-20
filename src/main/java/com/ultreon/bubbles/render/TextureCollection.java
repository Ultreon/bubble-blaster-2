package com.ultreon.bubbles.render;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.common.Registrable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextureCollection extends Registrable {
    private static final Logger LOGGER = LogManager.getLogger("Texture-Collection");
    private final HashMap<Index, Image> textures = new HashMap<>();

    public TextureCollection() {

    }

    public void set(Index index, ITexture texture) {
        if (textures.containsKey(index)) {
            LOGGER.warn("Texture override: " + index);
        }

        BufferedImage bufferedImage = new BufferedImage(texture.width(), texture.height(), BufferedImage.TYPE_INT_ARGB);
        Renderer renderer = new Renderer(bufferedImage.getGraphics(), BubbleBlaster.getInstance().getObserver());
        renderer.hint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        texture.render(renderer);
        renderer.dispose();

        textures.put(index, bufferedImage);
    }

    public Image get(Index location) {
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
