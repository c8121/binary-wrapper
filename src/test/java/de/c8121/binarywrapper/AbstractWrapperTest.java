package de.c8121.binarywrapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class AbstractWrapperTest {

    static class TestWrapper extends AbstractWrapper {

        public TestWrapper() throws IOException {
            super("rsync", "rsync.exe");
        }
    }

    @Test
    void test() throws IOException {

        var binary = new TestWrapper().binaryFileLocation();
        System.out.println(binary);

    }

}