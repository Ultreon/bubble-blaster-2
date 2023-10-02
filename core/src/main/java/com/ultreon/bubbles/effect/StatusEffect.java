package com.ultreon.bubbles.effect;

import com.badlogic.gdx.graphics.Texture;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.event.v1.VfxEffectBuilder;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.TextureManager;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.exception.RegistryException;
import com.ultreon.libs.resources.v0.Resource;
import org.jetbrains.annotations.Nullable;

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
        if (key == null) throw new RegistryException("Object not registered: " + this.getClass().getName());
        return key.mapPath(path -> "effects/" + path);
    }

    public @Nullable Resource getIconResource() {
        Identifier resId = this.getIconId();
        @Nullable Resource stream = BubbleBlaster.getInstance().getResourceManager().getResource(resId);
        if (stream == null) {
            BubbleBlaster.getLogger().warn("Cannot find effect-icon: " + resId);
        }
        return stream;
    }

    public Texture getIcon() {
        if (this.cachedTexture != null) {
            return this.cachedTexture;
        }

        BubbleBlaster game = BubbleBlaster.getInstance();
        com.badlogic.gdx.graphics.Texture texture = game.getTextureManager().getOrLoadTexture(this.getIconId());
        return this.cachedTexture = Objects.requireNonNullElse(texture, FALLBACK_TEXTURE);
    }

    public final void tick(Entity entity, StatusEffectInstance appliedEffect) {
        if (this.canExecute(entity, appliedEffect)) {
            this.execute(entity, appliedEffect);
        }
    }

    protected abstract boolean canExecute(Entity entity, StatusEffectInstance appliedEffect);

    @SuppressWarnings("EmptyMethod")
    public void execute(Entity entity, StatusEffectInstance appliedEffect) {

    }

    public void buildVfx(StatusEffectInstance appliedEffect, VfxEffectBuilder builder) {

    }

    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {

    }

    public void onStop(Entity entity) {

    }

    @SuppressWarnings("EmptyMethod")
    protected void updateStrength() {

    }

    public Identifier getId() {
        return Registries.EFFECTS.getKey(this);
    }
}
