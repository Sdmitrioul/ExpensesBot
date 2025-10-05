package com.dskroba.actor;

import java.io.Closeable;

public interface PrincipalActor extends Closeable {
    boolean submit(Job job);
}
