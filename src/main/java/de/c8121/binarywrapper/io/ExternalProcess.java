package de.c8121.binarywrapper.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class ExternalProcess {


    public static class SyncResult {
        private int exitCode = 0;
        private final StringBuilder out = new StringBuilder();
        private final StringBuilder err = new StringBuilder();

        public int exitCode() {
            return this.exitCode;
        }

        public String out() {
            return this.out.toString();
        }

        public boolean hasErr() {
            return !this.err.isEmpty();
        }

        public String err() {
            return this.err.toString();
        }
    }


    private final ProcessBuilder builder;

    /**
     *
     */
    public ExternalProcess(String... command) {
        this.builder = new ProcessBuilder(command);
    }

    /**
     *
     */
    public ExternalProcess(List<String> command) {
        this.builder = new ProcessBuilder(command);
    }

    /**
     *
     */
    public ExternalProcess workingDir(final File dir) {
        this.builder.directory(dir);
        return this;
    }

    /**
     *
     */
    public ExternalProcess environmentVars(final Map<String, String> vars) {
        for (Map.Entry<String, String> e : vars.entrySet())
            this.builder.environment().put(e.getKey(), e.getValue());
        return this;
    }

    /**
     *
     */
    public ExternalProcess clearEnvironmentVars() {
        this.builder.environment().clear();
        return this;
    }

    /**
     *
     */
    public ExternalProcess redirectErrorStream(final boolean redirect) {
        this.builder.redirectErrorStream(redirect);
        return this;
    }

    /**
     * Starts the process.
     */
    public Process start() throws IOException {
        return this.builder.start();
    }

    /**
     * Run the process synchronous.
     * <p>
     * Output of process will be put to {@link SyncResult#out}.
     */
    public SyncResult run() throws IOException, InterruptedException {
        this.builder.redirectErrorStream(true);

        SyncResult result = new SyncResult();

        Process process = this.start();

        BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = out.readLine()) != null) {
            result.out.append(line).append("\n");
        }

        result.exitCode = process.waitFor();

        return result;
    }
}
