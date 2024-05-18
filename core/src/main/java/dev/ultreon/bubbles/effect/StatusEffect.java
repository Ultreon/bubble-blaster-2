package dev.ultreon.bubbles.effect;

import com.badlogic.gdx.graphics.Texture;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.event.v1.VfxEffectBuilder;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.render.TextureManager;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.registries.v0.exception.RegistryException;
import dev.ultreon.libs.resources.v0.Resource;
import dev.ultreon.libs.text.v1.Translatable;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public abstract class StatusEffect implements Translatable {
    // Empty Image.
    private static final Texture FALLBACK_TEXTURE;

    static {
        FALLBACK_TEXTURE = TextureManager.DEFAULT_TEX;
    }

    private Texture cachedTexture = null;

    public StatusEffect() {

    }

    public Identifier getIconId() {
        var key = Registries.EFFECTS.getKey(this);
        if (key == null) throw new RegistryException("Object not registered: " + this.getClass().getName());
        return key.mapPath(path -> "effects/" + path);
    }

    public @Nullable Resource getIconResource() {
        var resId = this.getIconId();
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

        var game = BubbleBlaster.getInstance();
        var texture = game.getTextureManager().getOrLoadTexture(this.getIconId());
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

    protected void onStrengthUpdate(int oldValue, int newValue) {

    }

    public Identifier getId() {
        return Registries.EFFECTS.getKey(this);
    }

    @Override
    public String getTranslationPath() {
        var id = this.getId();
        return id.location() + ".statusEffect." + id.path().replaceAll("/", ".");
    }
}
