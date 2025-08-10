package com.dskroba.base.bean;

public interface Bean {
    void start();

    void stop();

    default void close() {
        stop();
    }
}
