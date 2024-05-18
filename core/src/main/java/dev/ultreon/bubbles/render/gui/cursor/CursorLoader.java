package dev.ultreon.bubbles.render.gui.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.libs.commons.v0.Identifier;

public abstract class CursorLoader {
    private final Identifier id;
    protected Vector2 hotspot;

    public CursorLoader(Identifier id) {
        this.id = id;
    }

    public final Cursor create() {
        return Gdx.graphics.newCursor(this.getPixmap(), (int) this.hotspot.x, (int) this.hotspot.y);
    }

    public final Pixmap getPixmap() {
        return new Pixmap(BubbleBlaster.resource(this.getId().mapPath(s -> "textures/cursor/" + s + ".png")));
    }
    public Identifier getId() {
        return this.id;
    }

    public Vector2 getHotspot() {
        return this.hotspot.cpy();
    }
}
