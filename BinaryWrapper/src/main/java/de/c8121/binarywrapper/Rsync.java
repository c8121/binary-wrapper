package de.c8121.binarywrapper;

import java.io.IOException;

public class Rsync extends AbstractWrapper {

    public static final String LINUX_BINARY_NAME = "rsync";
    public static final String WINDOWS_BINARY_NAME = "rsync.exe";

    /**
     *
     */
    public Rsync() throws IOException {
        super(LINUX_BINARY_NAME, WINDOWS_BINARY_NAME);
    }

    /**
     * <code>--version</code>
     */
    public Rsync version() {
        this.addCommandOption("--version");
        return this;
    }

    /**
     * <code>-v</code>
     */
    public Rsync verbose() {
        this.addCommandOption("-v");
        return this;
    }

    /**
     * <code>-a</code>
     */
    public Rsync archive() {
        this.addCommandOption("-a");
        return this;
    }

    /**
     * <code>-r</code>
     */
    public Rsync recursive() {
        this.addCommandOption("-r");
        return this;
    }

    /**
     * <code>--delete</code>
     */
    public Rsync delete() {
        this.addCommandOption("--delete");
        return this;
    }

    /**
     * Configures rsync to use ssh (<code>-e "ssh.exe -i keyFile"</code>)
     */
    public Rsync useSsh(String keyFile) throws IOException {
        this.addCommandOption("-e",
                new Ssh()
                        .keyFile(keyFile)
                        .buildForCli()
        );
        return this;
    }

    /**
     * Configures rsync to use ssh with sshpass (<code>-e "sshpass ... ssh.exe"</code>)
     * Please note the "SECURITY CONSIDERATIONS" in <code>man sshpass</code>
     */
    public Rsync useSshPass(char[] password) throws IOException {
        this.addCommandOption("-e",
                new Ssh()
                        .sshPass(password)
                        .buildForCli()
        );
        return this;
    }

    /**
     * Configures rsync to use a password file: <code>--password-file="..."</code>.
     * Note: This Option may only be used when accessing a rsync daemon.
     */
    public Rsync usePasswordFile(String passwordFile) {
        this.addCommandOption("--password-file=\"" + passwordFile + "\"");
        return this;
    }

    /**
     * Path to local source directory or file
     */
    public Rsync src(String src) {
        this.addCommandArgs(BINARY_PROVIDER.parsePathName(src));
        return this;
    }

    /**
     * Destination path or url like <code>[user@host:]path</code>
     */
    public Rsync dest(String dest) {
        this.addCommandArgs(BINARY_PROVIDER.parsePathName(dest));
        return this;
    }
}
