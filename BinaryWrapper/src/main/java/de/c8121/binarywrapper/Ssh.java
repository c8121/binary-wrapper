package de.c8121.binarywrapper;

import java.io.IOException;
import java.util.List;

public class Ssh extends AbstractWrapper {

    public static final String LINUX_BINARY_NAME = "ssh";
    public static final String WINDOWS_BINARY_NAME = "ssh.exe";

    private SshPass sshPass;

    /**
     *
     */
    public Ssh() throws IOException {
        super(LINUX_BINARY_NAME, WINDOWS_BINARY_NAME);
    }

    /**
     * Username and Host like in <code>ssh <strong>user@host</strong></code>
     */
    public Ssh userAndHost(String user, String host) {
        this.addCommandOption(user + "@" + host);
        return this;
    }

    /**
     * Set tcp-port to be used (only if port > 0)
     *
     * @param port port-number (call will have no effect if port <= 0)
     */
    public Ssh port(int port) {
        if (port > 0)
            this.addCommandOption("-p", Integer.toString(port));
        return this;
    }

    /**
     * Key-auth file, <code>-i</code> in ssh
     */
    public Ssh keyFile(String keyFilePath) {
        this.addCommandOption("-i", keyFilePath);
        return this;
    }

    /**
     * Use ssh-pass to pass the password to ssh.
     * Please note the "SECURITY CONSIDERATIONS" in <code>man sshpass</code>
     */
    public Ssh sshPass(char[] password) throws IOException {
        this.sshPass = new SshPass()
                .password(password);
        return this;
    }

    /**
     * Command to execute: <code>ssh command</code>
     */
    public Ssh command(String command) {
        this.addCommandArgs(command);
        return this;
    }

    /**
     * Requests ssh to go to background (<code>-f</code>)
     */
    public Ssh goToBackground() {
        this.addCommandOption("-f");
        return this;
    }

    /**
     * Do not execute a remote command.  This is useful for just forwarding ports. (<code>-N</code>)
     */
    public Ssh dontExecuteRemoteCommand() {
        this.addCommandOption("-N");
        return this;
    }

    /**
     * Forward local port (<code>-L port:host:hostport</code>)
     */
    public Ssh forwardLocal(int localPort, String remoteHost, int remotePort) {
        this.addCommandOption("-L", localPort + ":" + remoteHost + ":" + remotePort);
        return this;
    }

    @Override
    protected List<String> build() {
        var cmd = super.build();
        if (this.sshPass != null) {
            cmd.addAll(0, this.sshPass.build());
        }
        return cmd;
    }
}
