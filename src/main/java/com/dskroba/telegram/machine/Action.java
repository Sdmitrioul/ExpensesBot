package com.dskroba.telegram.machine;

public interface Action {
    State execute(State previous);

    default boolean isFullRow() {
        return false;
    }

    default int priority() {
        return 0;
    }

    default boolean isHidden() {
        return false;
    }

    String getDescription();
}
