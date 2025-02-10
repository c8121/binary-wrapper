package de.c8121.binarywrapper;

import de.c8121.binarywrapper.io.ProcessExecutor;
import de.c8121.binarywrapper.io.RunningProcess;
import de.c8121.binarywrapper.util.BinaryProvider;
import de.c8121.binarywrapper.util.CygwinProvider;
import de.c8121.binarywrapper.util.LinuxProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractWrapper extends ProcessExecutor {

    public final static String OS_NAME = java.lang.System.getProperty("os.name", "null").toLowerCase();
    public final static boolean IS_WINDOWS = OS_NAME.contains("windows");

    public final static BinaryProvider BINARY_PROVIDER = createBinaryProvider();

    private final Path binaryFileLocation;

    private final List<String> commandOptions = new ArrayList<>();
    private final List<String> commandArgs = new ArrayList<>();


    /**
     *
     */
    private static BinaryProvider createBinaryProvider() {
        return IS_WINDOWS ? new CygwinProvider() : new LinuxProvider();
    }


    /**
     *
     */
    public AbstractWrapper(String linuxBinaryName, String cygwinBinaryName) throws IOException {
        this.binaryFileLocation = BINARY_PROVIDER.build(IS_WINDOWS ? cygwinBinaryName : linuxBinaryName);
    }


    /**
     *
     */
    protected void addCommandOption(String name, String value) {

        Objects.requireNonNull(name);
        if (name.isEmpty()) throw new IllegalArgumentException("Empty name");

        Objects.requireNonNull(value);
        if (value.isEmpty()) throw new IllegalArgumentException("Empty value");

        this.commandOptions.add(name);
        this.commandOptions.add(value);
    }

    /**
     *
     */
    protected void addCommandOption(String name) {

        Objects.requireNonNull(name);
        if (name.isEmpty()) throw new IllegalArgumentException("Empty name");

        this.commandOptions.add(name);
    }

    /**
     *
     */
    protected void addCommandArgs(String arg) {

        Objects.requireNonNull(arg);
        if (arg.isEmpty()) throw new IllegalArgumentException("Empty arg");
        this.commandArgs.add(arg);

    }

    /**
     * Path to executable file
     */
    public Path binaryFileLocation() {
        return this.binaryFileLocation;
    }


    /**
     * Execute command asynchronous
     */
    public RunningProcess execute() throws IOException, InterruptedException {
        return this.execute(this.build());
    }

    /**
     * Execute command synchronous
     */
    public RunningProcess execute(long idleTimeoutMs) throws IOException, ExecutionException, TimeoutException, InterruptedException {
        return this.execute(this.build(), idleTimeoutMs);
    }

    /**
     * Build command line like <code>[binary, options, args]</code>
     * to be used in {@link ProcessExecutor}
     */
    protected List<String> build() {
        var cmdLine = new ArrayList<String>(1 + this.commandOptions.size() + this.commandArgs.size());
        cmdLine.add(this.binaryFileLocation.toString());
        cmdLine.addAll(this.commandOptions);
        cmdLine.addAll(this.commandArgs);

        return cmdLine;
    }

    /**
     * Build command line like <code>[binary, options, args]</code>
     * to be used in Shell.
     */
    public String buildForCli() {
        var s = new StringBuilder();

        var cmd = this.build();
        for (var i : cmd) {
            if (!s.isEmpty()) s.append(" ");
            if (i.contains(" "))
                s.append('\'').append(i).append('\'');
            else
                s.append(i);
        }
        return s.toString();
    }
}
