package com.dskroba.statemachine.handlers.data;

import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.handlers.StateSelector;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors;
import com.dskroba.statemachine.state.descriptors.DataStatesDescriptors;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.SelectOption;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.dskroba.statemachine.state.descriptors.DataStatesDescriptors.CHOOSE_OPERATION;

@Component
public class ChooseOperationHandler extends StateSelector {
    private static final Map<String, StateDescriptor> EVENT_STATE_MAP = Map.of(
            "import", CommandStatesDescriptors.IMPORT_DATA,
            "export", CommandStatesDescriptors.EXPORT_DATA
    );
    private static final State CHOOSE_OPERATION_STATE_INSTANCE = State.cancelable(
            DataStatesDescriptors.CHOOSE_OPERATION,
            SelectEffect.withCancelOption("Choose command", List.of(
                    SelectOption.base("Import"),
                    SelectOption.base("Export"))));

    public ChooseOperationHandler() {
        super(EVENT_STATE_MAP);
    }

    @Override
    public State createState() {
        return CHOOSE_OPERATION_STATE_INSTANCE;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return CHOOSE_OPERATION;
    }
}
