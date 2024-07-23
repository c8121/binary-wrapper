package de.c8121.binarywrapper.io;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class RunningProcess {

    private final Process process;
    private int exitCode = 0;

    private final ProcessInputStreamReader out;
    private final ProcessInputStreamReader err;

    public RunningProcess(Process process) {
        this.process = process;
        this.out = new ProcessInputStreamReader(process.getInputStream());
        this.err = process.getErrorStream() != null ? new ProcessInputStreamReader(process.getErrorStream()) : null;
    }

    /**
     * Start reading process output.
     */
    public void startReading(ExecutorService executorService, Consumer<Integer> outConsumer, Consumer<Integer> errConsumer) {
        this.out.consumer(outConsumer).start(executorService);
        if (this.err != null)
            this.err.consumer(errConsumer).start(executorService);
    }

    /**
     * Causes current Thread to wait until the process has terminated.
     */
    public void waitForExit() throws InterruptedException {
        this.exitCode = this.process.waitFor();
    }

    /**
     * Exit code after {@link #waitForExit()}
     */
    public int exitCode() {
        return this.exitCode;
    }

    /**
     * Destroy process and all of its descendants.
     */
    public void destroy() {
        this.process.descendants().forEach(ProcessHandle::destroy);
        this.process.destroy();
    }

    /**
     * stdout
     */
    public ProcessInputStreamReader out() {
        return this.out;
    }

    /**
     * stderr
     */
    public ProcessInputStreamReader err() {
        return this.err;
    }

}
