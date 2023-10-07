package com.ultreon.bubbles.render;

import com.badlogic.gdx.graphics.Texture;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.resources.ByteArrayFileHandle;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.resources.v0.Resource;
import com.ultreon.libs.resources.v0.ResourceManager;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TextureManager {
    private static final TextureManager instance = new TextureManager();
    public static final Resource DEFAULT_TEX_RESOURCE;
    public static final Texture DEFAULT_TEX;

    static {
        DEFAULT_TEX_RESOURCE = new Resource(() -> {
            var image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            var graphics = image.getGraphics();
            graphics.setColor(Color.rgb(0xffbb00).toAwt());
            graphics.fillRect(0, 0, 16, 16);
            graphics.setColor(Color.rgb(0x333333).toAwt());
            graphics.fillRect(0, 8, 8, 8);
            graphics.fillRect(8, 0, 8, 8);
            var out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            graphics.dispose();
            out.flush();
            var byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());
            out.close();
            return byteArrayInputStream;
        }); // TODO: Replace with mem:// url.

        DEFAULT_TEX = new Texture(new ByteArrayFileHandle(".png", DEFAULT_TEX_RESOURCE.loadOrGet()));
    }
    private final Map<Identifier, Texture> textureMap = new ConcurrentHashMap<>();

    public static TextureManager instance() {
        return instance;
    }

    private TextureManager() {

    }

    public Texture getTexture(Identifier entry) {
        this.textureMap.get(entry);
        return this.textureMap.get(entry);
    }

    public Texture getOrLoadTexture(Identifier entry) {
        if (this.textureMap.containsKey(entry)) {
            return this.textureMap.get(entry);
        }

        return this.loadTexture(entry, new TextureSource() {
            @Override
            public Texture create() {
                @NotNull ResourceManager resourceManager = BubbleBlaster.getInstance().getResourceManager();
                var resource = resourceManager.getResource(entry.withPath("textures/" + entry.path() + ".png"));
                if (resource == null) resource = DEFAULT_TEX_RESOURCE;
                return new NativeImage(resource);
            }
        });
    }

    public Texture loadTexture(Identifier entry, TextureSource source) {
        var texture = source.create();
        this.textureMap.put(entry, texture);
        return texture;
    }
}
