package de.c8121.binarywrapper.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class ProcessInputStreamReader {

    private final InputStream in;

    private Consumer<Integer> consumer;

    private volatile long lastReadTs;


    /**
     * Read from <code>in</code>
     */
    public ProcessInputStreamReader(InputStream in) {
        this.in = Objects.requireNonNull(in);
    }

    /**
     * Consumer for input that will be read.
     */
    public ProcessInputStreamReader consumer(Consumer<Integer> consumer) {
        Objects.requireNonNull(consumer);
        this.consumer = consumer;
        return this;
    }

    /**
     * Time since last input has arrived
     */
    public long idleTimeMs() {
        return (System.nanoTime() - this.lastReadTs) / 1_000_000;
    }

    /**
     * Start reading
     */
    public void start(ExecutorService executor) {
        executor.submit(this::read);
    }

    /**
     *
     */
    private void read() {
        try {
            this.lastReadTs = System.nanoTime(); //Init

            int r;
            while ((r = this.in.read()) != -1) {
                this.lastReadTs = System.nanoTime();
                if (this.consumer != null)
                    this.consumer.accept(r);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
