package com.dskroba.statemachine.handlers.command;

import com.dskroba.statemachine.handlers.StateSelector;
import com.dskroba.statemachine.state.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MenuHandler extends StateSelector {
    private static final Map<String, State> EVENT_STATE_MAP = Map.of(
            "expenses", ExpenseStates.CHOOSE_TAG,
            "statistic", StatisticStates.STATISTIC_STATE,
            "data", DataStates.CHOOSE_OPERATION
    );

    public MenuHandler() {
        super(EVENT_STATE_MAP);
    }

    @Override
    public State state() {
        return CommandStates.MENU;
    }
}
