package de.c8121.binarywrapper.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LinuxProvider implements BinaryProvider {

    public static final String[] DEFAULT_LOCATIONS = new String[]{
            "/usr/bin"
    };

    @Override
    public Path build(String binaryName) throws IOException {
        for (var dirName : DEFAULT_LOCATIONS) {
            if (dirName != null && !dirName.isBlank()) {
                var dir = Paths.get(dirName);
                if (Files.exists(dir) && Files.isDirectory(dir)) {
                    var binary = dir.resolve(binaryName);
                    if (Files.exists(binary) && Files.isExecutable(binary))
                        return binary;
                }
            }
        }
        throw new FileNotFoundException(binaryName);
    }
}
