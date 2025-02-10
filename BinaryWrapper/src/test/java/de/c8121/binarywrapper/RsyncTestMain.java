package de.c8121.binarywrapper;

import de.c8121.binarywrapper.testutil.TestCli;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

class RsyncTestMain {


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {

        var executor = Executors.newSingleThreadExecutor();

        Rsync rsync = new Rsync()
                .useSshPass("test".toCharArray())
                .src(TestCli.ask("Source", ""))
                .dest(TestCli.ask("Destination", ""))
                .executorService(executor);

        System.out.println(rsync.execute(5000).exitCode());

        executor.shutdownNow();
    }

}