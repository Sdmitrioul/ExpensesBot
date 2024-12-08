package com.dskroba.telegram.machine;

public interface Action {
    State execute(State previous);

    String getDescription();
}
