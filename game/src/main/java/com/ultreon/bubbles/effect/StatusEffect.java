package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.Registrable;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.event.v2.FilterBuilder;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.helpers.SvgHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public abstract class StatusEffect extends Registrable {
    // Empty Image.
    protected static final Image image;

    static {
        image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Renderer renderer = new Renderer(image.getGraphics(), BubbleBlaster.getInstance().getObserver());
        renderer.clearColor(new Color(0, 0, 0, 0));
        renderer.clearRect(0, 0, 32, 32);
    }

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public StatusEffect() {

    }

    public URL getIconResource() {
        return getClass().getResource("assets/" + id().location() + "/vectors/effects/" + id().path() + ".svg");
    }

    public InputStream getIconResourceAsStream() {
        if (cache.containsKey("icon-stream")) {
            return (InputStream) cache.get("icon-stream");
        }
        Identifier resId = new Identifier("vectors/effects/" + id().path() + ".svg", id().location());
        InputStream stream = BubbleBlaster.getInstance().getResourceManager().openResourceStream(resId);
        if (stream == null && getIconResource() == null) {
            BubbleBlaster.getLogger().warn("Cannot find effect-icon: " + resId);
        }
        return (InputStream) cache.put("icon-stream", stream);
    }

    public Image getIcon(int w, int h, Color color) throws IOException {
//        if (cache.containsKey("icon-img")) {
//            return (Image) cache.get("icon-img");
//        }
//
//        Image img;
//        InputStream inputStream = getIconResourceAsStream();
//        if (inputStream != null) {
//            SvgHelper svgHelper = new SvgHelper(getIconResource());
//            img = svgHelper.getColoredImage(w, h, color);
//        } else {
//            Game.getLogger().warn("Cannot find effect-icon: " + getIconResource().toString());
//            img = image;
//        }
//
//        cache.put("icon-img", img);
//
//        return img;

        InputStream inputStream = getIconResourceAsStream();
        if (inputStream != null && getIconResource() != null) {
            SvgHelper svgHelper = new SvgHelper(getIconResource());
            return svgHelper.getColoredImage(w, h, color);
        }
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusEffect that = (StatusEffect) o;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
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

    @Override
    public String toString() {
        return "StatusEffect[" + id() + "]";
    }
}
