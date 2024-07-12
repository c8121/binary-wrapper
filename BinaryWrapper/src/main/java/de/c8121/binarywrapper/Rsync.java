package de.c8121.binarywrapper;

import java.io.IOException;

public class Rsync extends AbstractWrapper {

    public static final String LINUX_BINARY_NAME = "rsync";
    public static final String WINDOWS_BINARY_NAME = "rsync.exe";

    /**
     *
     */
    public Rsync() throws IOException {
        super(LINUX_BINARY_NAME, WINDOWS_BINARY_NAME);
    }

    public Rsync version() {
        this.addCommandOption("--version");
        return this;
    }

    public Rsync recursive() {
        this.addCommandOption("-r");
        return this;
    }

    public Rsync src(String src) {
        this.addCommandArgs(BINARY_PROVIDER.parsePathName(src));
        return this;
    }

    public Rsync dest(String dest) {
        this.addCommandArgs(BINARY_PROVIDER.parsePathName(dest));
        return this;
    }
}
