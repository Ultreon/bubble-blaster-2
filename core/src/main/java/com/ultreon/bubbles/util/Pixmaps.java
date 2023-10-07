package com.ultreon.bubbles.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;

public class Pixmaps {
    public static Pixmap createFromFrameBuffer (int x, int y, int w, int h) {
        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);

        final var pixmap = new Pixmap(w, h, Pixmap.Format.RGB888);
        var pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, pixels);

        return pixmap;
    }
}
