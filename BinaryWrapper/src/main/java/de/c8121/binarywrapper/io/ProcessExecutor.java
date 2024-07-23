package de.c8121.binarywrapper.io;

import de.c8121.binarywrapper.AbstractWrapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class ExternalProcess {

    private static ExecutorService defaultExecutorService = null;
    private ExecutorService executorService = null;


    /**
     * {@link ExecutorService} to be used.
     * <p>
     * <b>Optionally</b>: {@code execute()} will create an {@code ExecutorService} if none was set before: {@code Executors.newCachedThreadPool()}
     */
    public <T extends ExternalProcess> T executorService(ExecutorService executorService) {
        this.executorService = executorService;
        //noinspection unchecked
        return (T) this;
    }

    /**
     *
     */
    private ExecutorService getExecutorService() {
        if (this.executorService == null) {
            this.executorService = defaultExecutorService;
            if (this.executorService == null) {
                synchronized (AbstractWrapper.class) {
                    var es = Executors.newCachedThreadPool();
                    defaultExecutorService = es;
                    this.executorService = es;
                }
            }
        }
        return this.executorService;
    }


    /**
     * Run the process asynchronous.
     */
    public ProcessResult execute(List<String> command) throws IOException {

        var executorService = this.getExecutorService();

        var builder = new ProcessBuilder(command);
        var result = new ProcessResult(builder.start());
        result.out().start(executorService);
        result.err().start(executorService);

        return result;
    }

    /**
     * Run the process synchronous.
     */
    public ProcessResult execute(List<String> command, long idleTimeoutMs) throws IOException, ExecutionException, TimeoutException, InterruptedException {

        var executorService = this.getExecutorService();

        var builder = new ProcessBuilder(command);
        var result = new ProcessResult(builder.start());
        result.out().start(executorService);
        result.err().start(executorService);

        var future = executorService.submit(() -> {
            try {
                result.waitForExit();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            future.get(idleTimeoutMs, TimeUnit.MILLISECONDS);
        } finally {
            result.process().descendants().forEach(ProcessHandle::destroy);
            result.process().destroy();
        }

        return result;

    }
}
