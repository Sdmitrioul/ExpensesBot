package com.dskroba.base;

public interface Listener<T> {
    void receive(T item);
}
