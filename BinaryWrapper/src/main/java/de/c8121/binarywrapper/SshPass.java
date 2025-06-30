package de.c8121.binarywrapper;

import java.io.IOException;
import java.util.List;

public class SshPass extends AbstractWrapper {

    public static final String LINUX_BINARY_NAME = "sshpass";
    public static final String WINDOWS_BINARY_NAME = "sshpass.exe";

    private AbstractWrapper command;

    /**
     *
     */
    public SshPass() throws IOException {
        super(LINUX_BINARY_NAME, WINDOWS_BINARY_NAME);
    }

    /**
     * <code>sshpass -p...</code>
     */
    public SshPass password(char[] password) {
        this.addCommandOption("-p", new String(password));
        return this;
    }

    /**
     * The command to run after sshpass own options.
     * Typically, it will be "ssh" with arguments
     */
    public SshPass command(AbstractWrapper command) {
        this.command = command;
        return this;
    }

    @Override
    protected List<String> build() {

        var cmd = super.build();

        if (command != null)
            cmd.addAll(this.command.build());

        return cmd;
    }
}
