package de.c8121.binarywrapper.util;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Provides the path to binary (executable) files.
 * Parses path names if necessary.
 */
public interface BinaryProvider {

    /**
     * Build path of executable
     */
    Path build(String binaryName) throws IOException;

    /**
     * Parse pathname (see {@link CygwinProvider#parsePathName(String)} for example).
     */
    default String parsePathName(String pathName) {
        return pathName;
    }
}
