package com.ultreon.commons.util;

import com.ultreon.libs.commons.v0.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FileUtils extends UtilityClass {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteDir(File file) throws IOException {
        var pathToBeDeleted = file.toPath();

        try (var walker = Files.walk(pathToBeDeleted)) {
            walker.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}