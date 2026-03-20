package com.dskroba.statemachine.handlers.command;

import com.dskroba.base.ClockProvider;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.SelectOption;
import com.dskroba.type.Timeframe;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors.EXPORT_DATA;
import static com.dskroba.statemachine.state.descriptors.DataStatesDescriptors.CHOOSE_OUTPUT_FORMAT;
import static com.dskroba.statemachine.state.descriptors.DataStatesDescriptors.TIMEFRAME_ATTRIBUTE_NAME;
import static com.dskroba.type.DateFilter.*;

@Component
public class ExportDataHandler extends AbstractHandlerWithFactory {
    private static final State EXPORT_DATA_STATE_INSTANCE = State.cancelable(
            CommandStatesDescriptors.EXPORT_DATA,
            SelectEffect.withCancelOption("Select historical period.", List.of(
                    SelectOption.base("Current Month"),
                    SelectOption.base("Previous Month"),
                    SelectOption.base("All history"))));

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(this::parseDateInterval)
                .map(this::setTimeframeAndChangeState);
    }

    private Effect setTimeframeAndChangeState(Timeframe timeframe) {
        return moveToState(CHOOSE_OUTPUT_FORMAT,
                state -> state.setAttribute(TIMEFRAME_ATTRIBUTE_NAME, timeframe));
    }

    private Timeframe parseDateInterval(String message) {
        return Optional.ofNullable(switch (message.toLowerCase()) {
                    case "all history" -> ALL_HISTORY;
                    case "current month" -> MONTH;
                    case "previous month" -> PREVIOUS_MONTH;
                    default -> null;
                }).map(filter -> filter.getTimeframe(ClockProvider.getClock()))
                .orElse(null);
    }

    @Override
    public State createState() {
        return EXPORT_DATA_STATE_INSTANCE;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return EXPORT_DATA;
    }
}
