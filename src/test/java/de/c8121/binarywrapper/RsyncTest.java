package de.c8121.binarywrapper;

import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RsyncTest {

    public static class TestRsync extends Rsync {
        public TestRsync() throws IOException {
            super();
        }

        @Override
        protected List<String> build() {
            var cmd = super.build();
            System.out.println(cmd);
            return cmd;
        }
    }


    @Test
    void test() throws IOException, InterruptedException {
        var cmd = new TestRsync()
                .version();

        var result = cmd.run();
        assertNotNull(result);
        assertFalse(result.out().isBlank());

        System.out.println(result.out());
    }

    @Test
    void testSrcDstArgs() throws IOException, InterruptedException {

        var s = "test.txt";

        var srcDir = Files.createTempDirectory("RsyncTestSource");
        var srcFile = srcDir.resolve(s);
        try (var out = Files.newOutputStream(srcFile)) {
            out.write(s.getBytes(StandardCharsets.UTF_8));
        }

        var dstDir = Files.createTempDirectory("RsyncTestDestination");

        var result = new TestRsync()
                .dest(srcDir + "/")
                .src(dstDir.toString())
                .recursive()
                .run();

        assertNotNull(result);
        System.out.println(result.out());

        Files.delete(srcFile);
        Files.delete(srcDir);

        var dstFile = dstDir.resolve(s);
        assertTrue(Files.exists(dstFile));

        Files.delete(dstFile);
        Files.delete(dstDir);

    }
}