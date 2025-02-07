package de.c8121.binarywrapper.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CygwinProvider implements BinaryProvider {

    public final static String[] CYGWIN_INSTALL_DIRS = new String[]{
            "C:\\cygwin64\\bin",
            "C:\\cygwin\\bin"
    };

    private volatile boolean cygwinReady = false;

    /**
     * Get Path of cygwin installation
     */
    static Path getCygwinLocation() throws FileNotFoundException {
        for (var dirName : CYGWIN_INSTALL_DIRS) {
            var path = Paths.get(dirName);
            if (Files.exists(path)) {
                return path;
            }
        }
        throw new FileNotFoundException("Could not find cygwin install directory");
    }


    private Path cygwinLocation;


    /**
     * Optionally set location of Cygwin-Installation (bin-dir).
     * If not set, this tries to find the location in {@link #CYGWIN_INSTALL_DIRS}
     */
    public CygwinProvider cygwinLocation(Path path) {
        this.cygwinLocation = path;
        return this;
    }

    /**
     *
     */
    @Override
    public Path build(String binaryName) throws IOException {

        var dir = this.getInitializedCygwinLocation();
        var binary = dir.resolve(binaryName);
        if (!Files.exists(binary))
            throw new FileNotFoundException(binary.toString());

        return binary;
    }

    /**
     * Check {@link #cygwinLocation}
     */
    private Path getInitializedCygwinLocation() throws IOException {

        if (!this.cygwinReady) {
            synchronized (CygwinProvider.class) {
                if (!this.cygwinReady) {
                    this.cygwinLocation = this.cygwinLocation != null ? this.cygwinLocation : getCygwinLocation();
                    this.cygwinReady = true;
                }
            }
        }

        return this.cygwinLocation;
    }


    /**
     *
     */
    @Override
    public String parsePathName(String pathName) {
        if (pathName.length() > 2 && pathName.charAt(1) == ':') {
            pathName = "/cygdrive/" + pathName.charAt(0) +
                    pathName.substring(2)
                            .replace('\\', '/');
        }
        return pathName;
    }
}
