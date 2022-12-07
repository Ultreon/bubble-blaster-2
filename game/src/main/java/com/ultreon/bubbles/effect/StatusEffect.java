package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.event.v2.FilterBuilder;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.resources.Resource;
import com.ultreon.bubbles.util.helpers.SvgHelper;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class StatusEffect {
    // Empty Image.
    private static final Image FALLBACK_IMAGE;

    static {
        FALLBACK_IMAGE = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Renderer renderer = new Renderer(FALLBACK_IMAGE.getGraphics(), BubbleBlaster.getInstance().getObserver());
        renderer.clearColor(new Color(0, 0, 0, 0));
        renderer.clearRect(0, 0, 32, 32);
    }

    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private Image cachedImage = null;

    public StatusEffect() {

    }

    public Identifier getIconId() {
        return Registry.EFFECTS.getKey(this).mapPath(path -> "vectors/effects/" + path + ".svg");
    }

    public @Nullable Resource getIconResource() {
        Identifier resId = getIconId();
        @Nullable Resource stream = BubbleBlaster.getInstance().getResourceManager().getResource(resId);
        if (stream == null) {
            BubbleBlaster.getLogger().warn("Cannot find effect-icon: " + resId);
        }
        return stream;
    }

    public Image getIcon(int w, int h, Color color) throws IOException {
        if (cachedImage != null) {
            return cachedImage;
        }

        Resource resource = getIconResource();

        if (resource == null) return cachedImage = FALLBACK_IMAGE;

        try (InputStream inputStream = resource.openStream()) {
            if (inputStream != null) {
                SvgHelper svgHelper = new SvgHelper(resource.getUrl(), inputStream);
                return cachedImage = svgHelper.getColoredImage(w, h, color);
            }
        } catch (Exception ignored) {

        }

        return cachedImage = FALLBACK_IMAGE;
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
