package com.ultreon.commons.util;

import com.ultreon.libs.commons.v0.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class FileUtils extends UtilityClass {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteDir(File file) throws IOException {
        Path pathToBeDeleted = file.toPath();

        try (Stream<Path> walker = Files.walk(pathToBeDeleted)) {
            walker.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}