package com.dskroba;

import java.util.function.Function;

public final class FP {
    public static <F, S, T> Function<F, T> compose(Function<F, S> first, Function<S, T> second) {
        return first.andThen(second);
    }

    private FP() {
    }
}
