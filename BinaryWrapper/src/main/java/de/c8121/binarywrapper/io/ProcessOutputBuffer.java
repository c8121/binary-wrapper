package de.c8121.binarywrapper.io;

import java.util.function.Consumer;

public class ProcessOutputBuffer implements Consumer<Integer> {

    private final StringBuilder buf = new StringBuilder();

    @Override
    public void accept(Integer integer) {
        this.buf.append((char) integer.intValue());
    }

    public StringBuilder buf() {
        return this.buf;
    }
}
