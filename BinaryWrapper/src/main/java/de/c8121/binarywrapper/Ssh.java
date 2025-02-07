package de.c8121.binarywrapper;

import java.io.IOException;

public class Ssh extends AbstractWrapper {

    public static final String LINUX_BINARY_NAME = "ssh";
    public static final String WINDOWS_BINARY_NAME = "ssh.exe";

    /**
     *
     */
    public Ssh() throws IOException {
        super(LINUX_BINARY_NAME, WINDOWS_BINARY_NAME);
    }
}
