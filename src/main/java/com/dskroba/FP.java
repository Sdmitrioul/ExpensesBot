package com.dskroba;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FP {
    public static <F, S, T> Function<F, T> compose(Function<F, S> first, Function<S, T> second) {
        return first.andThen(second);
    }

    public static <T> List<T> prepend(T element, List<T> list) {
        List<T> result = new ArrayList<>();
        result.add(element);
        result.addAll(list);
        return result;
    }

    private FP() {
    }
}
