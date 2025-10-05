package com.dskroba.statemachine.handlers.data;

import com.dskroba.statemachine.handlers.StateSelector;
import com.dskroba.statemachine.state.CommandStates;
import com.dskroba.statemachine.state.DataStates;
import com.dskroba.statemachine.state.State;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChooseOperationHandler extends StateSelector {
    private static final Map<String, State> EVENT_STATE_MAP = Map.of(
            "import", CommandStates.IMPORT_DATA,
            "export", CommandStates.EXPORT_DATA
    );

    public ChooseOperationHandler() {
        super(EVENT_STATE_MAP);
    }

    @Override
    public State state() {
        return DataStates.CHOOSE_OPERATION;
    }
}
