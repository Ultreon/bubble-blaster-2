package com.ultreon.bubbles.effect;

import com.badlogic.gdx.graphics.Texture;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.event.v1.FilterBuilder;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.TextureManager;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.exception.RegistryException;
import com.ultreon.libs.resources.v0.Resource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;


public abstract class StatusEffect {
    // Empty Image.
    private static final com.badlogic.gdx.graphics.Texture FALLBACK_TEXTURE;

    static {
        FALLBACK_TEXTURE = TextureManager.DEFAULT_TEX;
    }

    private com.badlogic.gdx.graphics.Texture cachedTexture = null;

    public StatusEffect() {

    }

    public Identifier getIconId() {
        Identifier key = Registries.EFFECTS.getKey(this);
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
        com.badlogic.gdx.graphics.Texture texture = game.getTextureManager().getOrLoadTexture(getIconId());
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

    public Identifier getId() {
        return Registries.EFFECTS.getKey(this);
    }
}
