package dev.ultreon.bubbles.init;

import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.render.TextureCollection;
import dev.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Initialization for texture collections.
 *
 * @see Registries#TEXTURE_COLLECTIONS
 * @since 1.0.924-a1
 */
public class TextureCollections {
//    public static final TextureCollection BUBBLE_TEXTURES = register("bubble", new TextureCollection());

    @SuppressWarnings("SameParameterValue")
    private static <T extends TextureCollection> T register(String name, T textureCollection) {
        Registries.TEXTURE_COLLECTIONS.register(new Identifier(name), textureCollection);
        return textureCollection;
    }

    @ApiStatus.Internal
    public static void register() {

    }
}
