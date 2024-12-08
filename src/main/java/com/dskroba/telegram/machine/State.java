package com.dskroba.telegram.machine;

import java.util.List;
import java.util.Optional;

public interface State {
    List<Action> getAllowedActions();

    State applyAction(Action action);

    default Optional<String> message() {
        return Optional.empty();
    }

    default Action parseAction(String text) {
        return getAllowedActions().stream().filter(action -> action.getDescription().equals(text)).findFirst().orElse(null);
    }
}
