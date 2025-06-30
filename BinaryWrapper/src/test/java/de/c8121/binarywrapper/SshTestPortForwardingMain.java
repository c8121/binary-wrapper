package de.c8121.binarywrapper;

import de.c8121.binarywrapper.testutil.TestCli;

import java.io.IOException;
import java.util.concurrent.Executors;

class SshTestPortForwardingMain {


    public static void main(String[] args) throws IOException, InterruptedException {

        var executor = Executors.newSingleThreadExecutor();

        Ssh ssh = new Ssh()
                .userAndHost(
                        TestCli.ask("User", System.getProperty("user.name")),
                        TestCli.ask("Host", null)
                )
                .sshPass(TestCli.askPassword("Password").toCharArray())
                .forwardLocal(
                        Integer.parseInt(TestCli.ask("Local Port", "13306")),
                        TestCli.ask("Remote Host", "localhost"),
                        Integer.parseInt(TestCli.ask("Remote Port", "14527"))
                )
                .executorService(executor);

        ssh.execute();

        System.out.println("Port forwarding started");
    }

}