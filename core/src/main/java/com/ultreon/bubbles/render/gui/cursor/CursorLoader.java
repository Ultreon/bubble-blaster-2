package com.ultreon.bubbles.render.gui.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.resources.ResourceFileHandle;
import com.ultreon.libs.commons.v0.Identifier;

public abstract class CursorLoader {
    private final Identifier id;
    protected Vector2 hotspot;

    public CursorLoader(Identifier id) {
        this.id = id;
    }

    public final Cursor create() {
        return Gdx.graphics.newCursor(getPixmap(), (int) hotspot.x, (int) hotspot.y);
    }

    public final Pixmap getPixmap() {
        return new Pixmap(new ResourceFileHandle(getId().mapPath(s -> "textures/cursor/" + s + ".png")));
    }
    public Identifier getId() {
        return id;
    }

    public Vector2 getHotspot() {
        return hotspot.cpy();
    }
}
