package com.dskroba.base.bean;

public interface Bean extends AutoCloseable {
    void start();
    void stop();

    default void close() {
        stop();
    }
}
