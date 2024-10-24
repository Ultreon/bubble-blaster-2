package dev.ultreon.bubbles.util.io;

import java.io.File;
import java.io.FileFilter;

public class ExtensionFileFilter implements FileFilter {
    private String extension;

    /**
     * File filtering with extension.
     *
     * @param extension the extension to filter files with. NOTE: This doesn't need to start with a dot. A dot will automatically be added when filtering/
     */
    public ExtensionFileFilter(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean accept(File file) {
        if (file.isFile()) {
            return file.getName().endsWith("." + this.extension);
        }
        return false;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
