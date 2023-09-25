package com.ultreon.bubbles;

import com.ultreon.libs.commons.v0.IDownloader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Downloader implements IDownloader {
    private final URL url;
    private final OutputStream outputStream;
    private final int blockSize;
    private long bytesDownloaded;
    private long length;
    private boolean paused;

    public Downloader(URL url, File output, int blockSize) throws IOException {
        this(url, new FileOutputStream(output), blockSize);
    }

    public Downloader(URL url, OutputStream outputStream, int blockSize) {
        this.url = url;
        this.outputStream = outputStream;
        this.blockSize = blockSize;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void downloadSync() throws IOException {
        URLConnection connection = this.url.openConnection();
        this.length = connection.getContentLengthLong();
        try (BufferedInputStream in = new BufferedInputStream(this.url.openStream())) {
            try (OutputStream fileOutputStream = this.outputStream) {
                byte[] dataBuffer = new byte[1024];

                byte[] buf = new byte[this.blockSize];
                int bytesRead;
                while ((bytesRead = in.read(buf)) >= 0) {
                    if (this.paused) {
                        Thread.sleep(10);
                        continue;
                    }
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    this.bytesDownloaded += bytesRead;
                }
            } catch (InterruptedException ignored) {

            }
        }
    }

    @Override
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override
    public long getBytesDownloaded() {
        return this.bytesDownloaded;
    }

    @Override
    public long getLength() {
        return this.length;
    }

    @Override
    public float getPercent() {
        if (this.length == -1) return Float.MIN_VALUE;
        return 100 * this.getRatio();
    }

    @Override
    public float getRatio() {
        if (this.length == -1) return Float.MIN_VALUE;
        return (float) ((double) this.bytesDownloaded / (double) this.length);
    }
}
}
