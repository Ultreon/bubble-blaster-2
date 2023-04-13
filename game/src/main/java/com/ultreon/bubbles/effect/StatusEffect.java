package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.event.v2.FilterBuilder;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.registry.RegistryException;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.Texture;
import com.ultreon.bubbles.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.awt.image.Raster;
import java.io.IOException;
import java.util.Objects;


public abstract class StatusEffect {
    // Empty Image.
    private static final Texture FALLBACK_TEXTURE;

    static {
        FALLBACK_TEXTURE = new Texture() {
            @Override
            protected int getWidth() {
                return 16;
            }

            @Override
            protected int getHeight() {
                return 16;
            }

            @Override
            public void draw(Renderer renderer, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight) {
                renderer.color(0xff404040);
                renderer.rect(x, y, width, height);
                renderer.color(0xffffc000);
                renderer.rect(x, y, width / 2, height / 2);
                renderer.rect(x + width / 2, y + height / 2, width / 2, height / 2);
            }

            @Override
            public Raster getRaster() {
                return null;
            }
        };
    }

    private Texture cachedTexture = null;

    public StatusEffect() {

    }

    public Identifier getIconId() {
        Identifier key = Registry.EFFECTS.getKey(this);
        if (key == null) throw new RegistryException("Object not registered: " + getClass().getName());
        return key.mapPath(path -> "effects/" + path);
    }

    public @Nullable Resource getIconResource() {
        Identifier resId = getIconId();
        @Nullable Resource stream = BubbleBlaster.getInstance().getResourceManager().getResource(resId);
        if (stream == null) {
            BubbleBlaster.getLogger().warn("Cannot find effect-icon: " + resId);
        }
        return stream;
    }

    public Texture getIcon() throws IOException {
        if (cachedTexture != null) {
            return cachedTexture;
        }

        BubbleBlaster game = BubbleBlaster.getInstance();
        var texture = game.getTextureManager().getOrLoadTexture(getIconId());
        return cachedTexture = Objects.requireNonNullElse(texture, FALLBACK_TEXTURE);
    }

    public final void tick(Entity entity, AppliedEffect appliedEffect) {
        if (canExecute(entity, appliedEffect)) {
            execute(entity, appliedEffect);
        }
    }

    protected abstract boolean canExecute(Entity entity, AppliedEffect appliedEffect);

    @SuppressWarnings("EmptyMethod")
    public void execute(Entity entity, AppliedEffect appliedEffect) {

    }

    public void onFilter(AppliedEffect appliedEffect, FilterBuilder builder) {

    }

    public void onStart(AppliedEffect appliedEffect, Entity entity) {

    }

    public void onStop(Entity entity) {

    }

    @Deprecated
    public AttributeContainer getAttributeModifiers() {
        return new AttributeContainer();
    }

    @SuppressWarnings("EmptyMethod")
    protected void updateStrength() {

    }
}
