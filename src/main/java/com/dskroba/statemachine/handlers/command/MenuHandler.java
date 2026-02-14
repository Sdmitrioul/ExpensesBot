package com.dskroba.statemachine.handlers.command;

import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.handlers.StateSelector;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateRecord;
import com.dskroba.statemachine.state.descriptors.*;
import com.dskroba.type.SelectOption;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MenuHandler extends StateSelector {
    private static final Map<String, StateDescriptor> EVENT_STATE_MAP = Map.of(
            "expenses", ExpensesStatesDescriptors.CHOOSE_TAG,
            "statistic", StatisticStatesDescriptors.STATISTIC_STATE,
            "data", DataStatesDescriptors.CHOOSE_OPERATION
    );

    private static final StateRecord MENU_STATE_INSTANCE = StateRecord.requiring(
            CommandStatesDescriptors.MENU,
            SelectEffect.of("Choose menu operation", List.of(
                    SelectOption.base("Expenses"),
                    SelectOption.base("Statistic"),
                    SelectOption.base("Data"))));

    public MenuHandler() {
        super(EVENT_STATE_MAP);
    }

    @Override
    public State createState() {
        return MENU_STATE_INSTANCE;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return CommandStatesDescriptors.MENU;
    }
}
