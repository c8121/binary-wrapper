package de.c8121.binarywrapper.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CygwinProvider implements BinaryProvider {

    public final static String FILES_RESOURCE_DIR = "cygwin64";
    public final static String FILE_LIST_RESOURCE = FILES_RESOURCE_DIR + "/files.txt";

    public final static String CYGWIN_DIR_NAME = ".cygwin64-for-java";

    private final static String[] CYGWIN_BASE_DIRS = new String[]{System.getenv("APPDATA"), System.getProperty("user.home") + "\\Local Settings\\Application Data", System.getProperty("user.home")};

    private volatile boolean cygwinReady = false;

    /**
     * Get location (directory) to save cygwin binaries to.
     */
    static Path getCygwinBaseDir() {

        for (var appdataPath : CYGWIN_BASE_DIRS) {
            if (appdataPath != null && !appdataPath.isBlank()) {
                var appdata = Paths.get(appdataPath);
                if (Files.exists(appdata) && Files.isDirectory(appdata) && Files.isWritable(appdata))
                    return appdata;
            }
        }
        return null;
    }

    /**
     * {@link #getCygwinBaseDir()} + {@link #CYGWIN_DIR_NAME} (create if not exists)
     */
    static Path getCygwinLocation() throws IOException {
        var base = getCygwinBaseDir();
        if (base == null) return null;

        var location = base.resolve(CYGWIN_DIR_NAME);
        if (!Files.exists(location)) {
            Files.createDirectory(location);
        }

        return location;
    }

    /**
     * Find all required files (listed in {@link #FILE_LIST_RESOURCE}).
     * {@link #FILE_LIST_RESOURCE} contains one filename per line (name without path).
     * Lines starting with '#' will be ignored.
     */
    static List<String> findFiles() throws IOException {
        try (
                var in = CygwinProvider.class.getClassLoader().getResourceAsStream(FILE_LIST_RESOURCE);
                var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))
        ) {
            var list = new ArrayList<String>();

            var line = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                list.add(line.trim());
            }

            return list;
        }
    }

    /**
     * Copy from resource dir {@link #FILES_RESOURCE_DIR} to {@code destination}
     */
    static void copy(String name, Path destination) throws IOException {

        var outPath = destination.resolve(name);

        try (
                var in = CygwinProvider.class.getClassLoader().getResourceAsStream(FILES_RESOURCE_DIR + "/" + name)
        ) {
            if (in == null)
                throw new IOException("Not found: " + name);

            try (var out = Files.newOutputStream(outPath)) {
                out.write(in.readAllBytes());
            }
        }

    }


    private Path cygwinLocation;
    private List<String> files;


    /**
     *
     */
    public CygwinProvider cygwinLocation(Path path) {
        this.cygwinLocation = path;
        return this;
    }

    /**
     *
     */
    public CygwinProvider files(String names) {
        this.files = List.of(names);
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
     * Check {@link #cygwinLocation}, copy all files from {@link #findFiles()} if not exists.
     */
    private Path getInitializedCygwinLocation() throws IOException {

        if (!this.cygwinReady) {
            synchronized (CygwinProvider.class) {
                if (!this.cygwinReady) {

                    this.cygwinLocation = this.cygwinLocation != null ? this.cygwinLocation : getCygwinLocation();
                    if (this.cygwinLocation == null)
                        throw new IOException("Cannot determine or create cygwin location");

                    //Copy Cygwin binaries if not exists, or if outdated
                    var files = this.files != null ? this.files : findFiles();
                    for (var file : files) {
                        var path = this.cygwinLocation.resolve(file);
                        if (!Files.exists(path)) {
                            copy(file, this.cygwinLocation);
                        } else if (Files.getLastModifiedTime(path).toMillis() < this.cygwinLocation.toFile().lastModified()) {
                            copy(file, this.cygwinLocation);
                        }
                    }

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
