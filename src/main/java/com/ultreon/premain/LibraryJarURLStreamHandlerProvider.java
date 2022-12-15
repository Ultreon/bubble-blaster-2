package com.ultreon.premain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;
import java.nio.file.ReadOnlyFileSystemException;

public final class LibraryJarURLStreamHandlerProvider extends URLStreamHandlerProvider {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("libraryjar".equals(protocol)) {
            return new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(u) {
                        private InputStream stream;

                        @Override
                        public void connect() throws IOException {
                            String host = u.getHost();
                            String path = u.getPath();
                            InputStream stream = PreMain.getManager().openConnectionInternal(u);
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
                            connect();
                            return stream;
                        }
                    };
                }
            };
        }
        return null;
    }

}