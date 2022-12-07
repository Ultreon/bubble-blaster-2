package com.ultreon.bubbles.mod.loader;

import com.ultreon.bubbles.game.BubbleBlaster;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.jar.JarInputStream;

public class LibraryJar {
    private final String libraryName;
    private final URL resourceUrl;

    public LibraryJar(URL url) {
        if (!Objects.equals(url.getProtocol(), "libraryjar")) {
            throw new IllegalArgumentException("Url protocol isn't 'libraryjar': " + url);
        }
        this.libraryName = url.getHost();
        this.resourceUrl = BubbleBlaster.getJarUrl();
    }

    public String getLibraryName() {
        return libraryName;
    }

    public JarInputStream openStream() throws IOException {
        URLConnection connection = resourceUrl.openConnection();
        connection.connect();
        return new JarInputStream(connection.getInputStream());
    }

    public URL getResourceUrl() {
        return resourceUrl;
    }
}
