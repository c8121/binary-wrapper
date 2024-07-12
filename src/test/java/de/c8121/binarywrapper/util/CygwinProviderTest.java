package de.c8121.binarywrapper.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CygwinProviderTest {

    @Test
    void test() throws IOException {
        var dir = new CygwinProvider().build("rsync.exe");
        assertNotNull(dir);
        System.out.println(dir);
    }

}