package com.ultreon.bubbles.mod.loader;

import com.ultreon.bubbles.game.BubbleBlaster;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.jar.JarInputStream;

public class LibraryJar {
    private final String libraryName;
    private final URL libraryUrl;

    public LibraryJar(URL url) {
        if (!Objects.equals(url.getProtocol(), "libraryjar")) {
            throw new IllegalArgumentException("Url protocol isn't 'libraryjar': " + url);
        }
        this.libraryName = url.getHost();
        this.libraryUrl = BubbleBlaster.getJarUrl();
    }

    public String getLibraryName() {
        return libraryName;
    }

    public JarInputStream openStream() throws IOException {
        URLConnection connection = libraryUrl.openConnection();
        return new JarInputStream(connection.getInputStream());
    }

    public InputStream openStream(String path) throws IOException {
        URL resourceUrl;
        try {
            resourceUrl = new URL("libraryjar", libraryUrl.getHost(), addSepAtStart(path));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        URLConnection connection = resourceUrl.openConnection();
        return connection.getInputStream();
    }

    private String addSepAtStart(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    public URL getLibraryUrl() {
        return libraryUrl;
    }

    public URL child(String path) {
        try {
            return new URL("libraryjar", libraryUrl.getHost(), addSepAtStart(path));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
