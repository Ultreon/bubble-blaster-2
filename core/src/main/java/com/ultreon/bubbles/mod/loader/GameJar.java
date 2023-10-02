package com.ultreon.bubbles.mod.loader;

import com.ultreon.bubbles.Main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarInputStream;

public class GameJar extends LibraryJar {
    private final URL jarUrl;

    public GameJar(URL jarUrl) {
        super(jarUrl);
        this.jarUrl = jarUrl;
    }

    @Override
    public JarInputStream openStream() throws IOException {
        return (JarInputStream) this.jarUrl.openStream();
    }

    @Override
    public InputStream openStream(String path) {
        return Main.class.getResourceAsStream("/" + path);
    }
}
