package de.c8121.binarywrapper;

import de.c8121.binarywrapper.testutil.TestCli;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

class SshTestMain {


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {

        var executor = Executors.newSingleThreadExecutor();

        Ssh ssh = new Ssh()
                .userAndHost(TestCli.ask("User", System.getProperty("user.name")), TestCli.ask("Host", null))
                .sshPass(TestCli.askPassword("Password").toCharArray())
                .command("uname -a && exit")
                .executorService(executor);

        System.out.println(ssh.execute(5000).exitCode());

        executor.shutdownNow();
    }

}