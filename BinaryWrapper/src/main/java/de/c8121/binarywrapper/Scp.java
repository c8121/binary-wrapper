package de.c8121.binarywrapper;

import java.io.IOException;
import java.util.List;

public class Scp extends AbstractWrapper {

    public static final String LINUX_BINARY_NAME = "scp";
    public static final String WINDOWS_BINARY_NAME = "scp.exe";

    private SshPass sshPass;

    /**
     *
     */
    public Scp() throws IOException {
        super(LINUX_BINARY_NAME, WINDOWS_BINARY_NAME);
    }

    /**
     * <code>-v</code>
     */
    public Scp verbose() {
        this.addCommandOption("-v");
        return this;
    }

    /**
     * <code>-r</code>
     */
    public Scp recursive() {
        this.addCommandOption("-r");
        return this;
    }

    /**
     * <code>-O</code>
     */
    public Scp legacyScp() {
        this.addCommandOption("-O");
        return this;
    }

    /**
     * Set tcp-port to be used (only if port > 0)
     *
     * @param port port-number (call will have no effect if port <= 0)
     */
    public Scp port(int port) {
        if (port > 0)
            this.addCommandOption("-P", Integer.toString(port));
        return this;
    }

    public Scp keyFile(String keyFilePath) {
        this.addCommandOption("-i", keyFilePath);
        return this;
    }

    /**
     * Use ssh-pass to pass the password to ssh.
     * Please note the "SECURITY CONSIDERATIONS" in <code>man sshpass</code>
     */
    public Scp sshPass(char[] password) throws IOException {
        this.sshPass = new SshPass()
                .password(password);
        return this;
    }

    /**
     * Path to local source directory or file
     */
    public Scp src(String src) {
        this.addCommandArgs(BINARY_PROVIDER.parsePathName(src));
        return this;
    }

    /**
     * Destination path or url like <code>[user@host:]path</code>
     */
    public Scp dest(String dest) {
        this.addCommandArgs(BINARY_PROVIDER.parsePathName(dest));
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
