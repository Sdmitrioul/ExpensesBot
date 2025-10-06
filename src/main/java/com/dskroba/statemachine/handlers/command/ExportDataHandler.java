package com.dskroba.statemachine.handlers.command;

import com.dskroba.actor.StateContext;
import com.dskroba.base.ClockProvider;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandler;
import com.dskroba.statemachine.state.CommandStates;
import com.dskroba.statemachine.state.State;
import com.dskroba.type.Timeframe;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.DataStates.CHOOSE_OUTPUT_FORMAT;
import static com.dskroba.statemachine.state.DataStates.TIMEFRAME_ATTRIBUTE_NAME;
import static com.dskroba.type.DateFilter.*;

@Component
public class ExportDataHandler extends AbstractHandler {
    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(this::parseDateInterval)
                .map(this::setTimeframeAndChangeState);
    }

    private List<Effect<?>> setTimeframeAndChangeState(Timeframe timeframe) {
        StateContext stateContext = StateContext.PROVIDER.get();
        stateContext.setAttribute(TIMEFRAME_ATTRIBUTE_NAME, timeframe);
        stateContext.setState(CHOOSE_OUTPUT_FORMAT);
        return List.of(CHOOSE_OUTPUT_FORMAT.initialEffect());
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
    public State state() {
        return CommandStates.EXPORT_DATA;
    }
}
