package dev.ultreon.bubbles;

import dev.ultreon.libs.commons.v0.IDownloader;
import dev.ultreon.libs.commons.v0.Mth;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * An examples showing how to automatically download, extract and load
 * Discord's native library.
 */
class DiscordSDKDownloader implements IDownloader {
    private final URL url;
    private File file;
    private boolean failed;
    private boolean completed;
    private int length;
    private long bytesDownloaded;

    public DiscordSDKDownloader() {
        this(Constants.DISCORD_GAME_SDK_VERSION);
    }

    public DiscordSDKDownloader(String version) {
        try {
            this.url = new URL("https://dl-game-sdk.discordapp.net/" + version + "/discord_game_sdk.zip");
        } catch (MalformedURLException e) {
            throw new DownloadException();
        }
    }

    @Override
    public void downloadSync() throws IOException {
        // Find out which name Discord's library has (.dll for Windows, .so for Linux)
        var name = "discord_game_sdk";
        String suffix;

        var osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        var arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        if (osName.contains("windows")) {
            suffix = ".dll";
        } else if (osName.contains("linux")) {
            suffix = ".so";
        } else if (osName.contains("mac os")) {
            suffix = ".dylib";
        } else {
            throw new IOException("cannot determine OS type: " + osName);
        }

		/*
		Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. macOS).
		At this point we need the "x86_64" version, as this one is used in the ZIP.
		 */
        if (arch.equals("amd64")) {
            arch = "x86_64";
        }

        // Path create Discord's library inside the ZIP
        var zipPath = "lib/" + arch + "/" + name + suffix;

        // Open the URL as a ZipInputStream
        var inputStream = this.url.openStream();

        this.length = inputStream.available();

        var zin = new ZipInputStream(inputStream);

        // Search for the right file inside the ZIP
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            var left = inputStream.available();
            this.bytesDownloaded = this.length - left;

            if (entry.getName().equals(zipPath)) {
                // Create a new temporary directory
                // We need to do this, because we may not change the filename on Windows
                var tempDir = new File(System.getProperty("java.io.tmpdir"), "java-" + name + System.nanoTime());
                if (!tempDir.mkdir()) {
                    throw new IOException("Cannot create temporary directory");
                }
                tempDir.deleteOnExit();

                // Create a temporary file inside our directory (with a "normal" name)
                var temp = new File(tempDir, name + suffix);
                temp.deleteOnExit();

                // Copy the file in the ZIP to our temporary file
                Files.copy(zin, temp.toPath());

                // We are done, so close the input stream
                zin.close();

                // Return our temporary file
                this.file = temp;
                this.failed = false;
                this.completed = true;
                return;
            }

            // next entry
            zin.closeEntry();
        }
        zin.close();
        // We couldn't find the library inside the ZIP
        this.file = null;
        this.failed = true;
        this.completed = true;
    }

    @Override
    public int getBlockSize() {
        return 512;
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
        return 100 * this.getRatio();
    }

    @Override
    public float getRatio() {
        return Mth.clamp(this.getBytesDownloaded() / this.getLength(), 0, 1);
    }

    public boolean isFailed() {
        return this.failed;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public URL getUrl() {
        return this.url;
    }

    public File getFile() {
        return this.file;
    }
}