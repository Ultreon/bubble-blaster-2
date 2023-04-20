package com.ultreon.bubbles.render;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.libs.resources.v0.Resource;
import com.ultreon.libs.resources.v0.ResourceManager;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class TextureManager {
    private static final TextureManager instance = new TextureManager();
    public static final Resource DEFAULT_TEXTURE;

    static {
        DEFAULT_TEXTURE = new Resource(() -> {
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Renderer graphics = new Renderer(image.getGraphics(), BubbleBlaster.getInstance().getObserver());
            graphics.color(Color.rgb(0xffbb00));
            graphics.rect(0, 0, 16, 16);
            graphics.color(Color.rgb(0x333333));
            graphics.rect(0, 8, 8, 8);
            graphics.rect(8, 0, 8, 8);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, ".png", out);
            graphics.dispose();
            out.close();
            return new ByteArrayInputStream(out.toByteArray());
        }); // TODO: Replace with mem:// url.
    }
    private final Map<Identifier, Texture> textureMap = new ConcurrentHashMap<>();

    public static TextureManager instance() {
        return instance;
    }

    private TextureManager() {

    }

    public Texture getTexture(Identifier entry) {
        textureMap.get(entry);
        return textureMap.get(entry);
    }

    public Texture getOrLoadTexture(Identifier entry) {
        if (textureMap.containsKey(entry)) {
            return textureMap.get(entry);
        }

        return loadTexture(entry, new TextureSource() {
            @Override
            public Texture create() {
                AwtImage awtImage = new AwtImage() {
                    @Override
                    protected byte[] loadBytes() {
                        @NotNull ResourceManager resourceManager = BubbleBlaster.getInstance().getResourceManager();
                        Resource resource = resourceManager.getResource(entry.withPath("textures/" + entry.path() + ".png"));
                        return Objects.requireNonNullElse(resource, DEFAULT_TEXTURE).loadOrGet();
                    }
                };
                awtImage.load();
                return awtImage;
            }
        });
    }

    public Texture loadTexture(Identifier entry, TextureSource source) {
        Texture texture = source.create();
        textureMap.put(entry, texture);
        return texture;
    }
}
