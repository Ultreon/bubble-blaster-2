package com.ultreon.premain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;

public final class LibraryJarURLStreamHandlerProvider extends URLStreamHandlerProvider {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("libraryjar".equals(protocol)) {
            return new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) {
                    return new URLConnection(u) {
                        private InputStream stream;

                        @Override
                        public void connect() throws IOException {
                            var host = u.getHost();
                            var path = u.getPath();
                            var stream = PreMain.getManager().openConnectionInternal(u);
                            if (stream == null) {
                                if (!PreMain.getManager().jarExists(host)) {
                                    throw new IOException("Library jar not found: " + host);
                                } else {
                                    throw new FileNotFoundException(host + path);
                                }
                            }
                            this.stream = stream;
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            this.connect();
                            return this.stream;
                        }
                    };
                }
            };
        }
        return null;
    }

}