package de.c8121.binarywrapper.io;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ProcessExecutor {

    private static ExecutorService defaultExecutorService = null;
    private ExecutorService executorService = null;


    private Consumer<Integer> outConsumer = (i) -> System.out.print((char) i.intValue());
    private Consumer<Integer> errConsumer = (i) -> System.err.print((char) i.intValue());

    /**
     * {@link ExecutorService} to be used.
     * <p>
     * <b>Optionally</b>: {@code execute()} will create an {@code ExecutorService} if none was set before: {@code Executors.newCachedThreadPool()}
     */
    public <T extends ProcessExecutor> T executorService(ExecutorService executorService) {
        this.executorService = executorService;
        //noinspection unchecked
        return (T) this;
    }

    /**
     * {@link Consumer<Integer>} for stdout
     */
    public <T extends ProcessExecutor> T outConsumer(Consumer<Integer> consumer) {
        this.outConsumer = consumer;
        //noinspection unchecked
        return (T) this;
    }

    /**
     * {@link Consumer<Integer>} for stderr
     */
    public <T extends ProcessExecutor> T errConsumer(Consumer<Integer> consumer) {
        this.errConsumer = consumer;
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
                synchronized (ProcessExecutor.class) {
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
    protected RunningProcess execute(List<String> command) throws IOException {

        var executorService = this.getExecutorService();

        var builder = new ProcessBuilder(command);
        var result = new RunningProcess(builder.start());
        result.startReading(executorService, this.outConsumer, this.errConsumer);

        return result;
    }

    /**
     * Run the process synchronous.
     */
    protected RunningProcess execute(List<String> command, long idleTimeoutMs) throws IOException, ExecutionException, TimeoutException, InterruptedException {

        var executorService = this.getExecutorService();

        var builder = new ProcessBuilder(command);
        var result = new RunningProcess(builder.start());
        result.startReading(executorService, this.outConsumer, this.errConsumer);

        var future = executorService.submit(() -> {
            try {
                result.waitForExit();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        while (true) {
            try {
                future.get(idleTimeoutMs, TimeUnit.MILLISECONDS);
                break; //exit in time.
            } catch (TimeoutException te) {
                var idleTime = Math.min(result.out().idleTimeMs(), result.err().idleTimeMs());
                if (idleTime >= idleTimeoutMs) {
                    result.destroy();
                    throw te; //timeout
                }
                //Detected output in time, process still running, no idle timeout
            } catch (Exception e) {
                result.destroy();
                throw e;
            }
        }

        return result;

    }
}
