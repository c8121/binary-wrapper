package de.c8121.binarywrapper;

import de.c8121.binarywrapper.io.ProcessOutputBuffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
    void testExecute() throws IOException, InterruptedException {

        var out = new ProcessOutputBuffer();
        var err = new ProcessOutputBuffer();

        TestRsync cmd = new TestRsync()
                .version()
                .outConsumer(out)
                .errConsumer(err);


        var result = cmd.execute();
        assertNotNull(result);


        System.out.println("Process started");
        result.waitForExit();

        assertFalse(out.buf().isEmpty());
        assertTrue(err.buf().isEmpty());

        System.out.println(out.buf());
    }

    @Test
    void testSrcDstArgs() throws IOException, InterruptedException, ExecutionException, TimeoutException {

        var s = "test.txt";

        var srcDir = Files.createTempDirectory("RsyncTestSource");
        var srcFile = srcDir.resolve(s);
        try (var out = Files.newOutputStream(srcFile)) {
            out.write(s.getBytes(StandardCharsets.UTF_8));
        }

        var dstDir = Files.createTempDirectory("RsyncTestDestination");
        var deleteFile = dstDir.resolve("delete.txt");
        try (var out = Files.newOutputStream(deleteFile)) {
            out.write(s.getBytes(StandardCharsets.UTF_8));
        }

        var out = new ProcessOutputBuffer();
        var err = new ProcessOutputBuffer();

        TestRsync result = new TestRsync()
                .outConsumer(out)
                .errConsumer(err);

        result.dest(srcDir + "/")
                .src(dstDir.toString())
                .recursive()
                .delete()
                .verbose()
                .execute(5000);

        assertNotNull(result);
        System.out.println(out.buf());

        Files.delete(srcFile);
        Files.delete(srcDir);

        var dstFile = dstDir.resolve(s);
        assertTrue(Files.exists(dstFile));
        assertFalse(Files.exists(deleteFile));

        Files.delete(dstFile);
        Files.delete(dstDir);

    }
}