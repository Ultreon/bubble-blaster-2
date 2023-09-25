package com.ultreon.bubbles.core;

import com.ultreon.bubbles.BubbleBlaster;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;
import java.util.Objects;

public class DataURLHandler extends URLStreamHandlerProvider {
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        return Objects.equals(protocol, "gamedata") ? new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return new File(BubbleBlaster.getGameDir(), u.getHost() + u.getPath()).toURI().toURL().openConnection();
            }
        } : null;
    }
}
