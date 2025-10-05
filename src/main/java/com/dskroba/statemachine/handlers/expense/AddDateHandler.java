package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.StateContext;
import com.dskroba.base.ClockProvider;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.expenses.ExpenseUtils;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.ExpenseStates;
import com.dskroba.statemachine.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.ExpenseStates.ADD_DESCRIPTION;
import static com.dskroba.statemachine.state.ExpenseStates.EXPENSE_ATTRIBUTE_NAME;

@Component
public class AddDateHandler extends SaveStateHandler {
    @Autowired
    protected AddDateHandler(ExpenseRepository repository) {
        super(repository);
    }

    @Override
    protected Optional<List<Effect<?>>> tryUpdateExpense(Event<?> event) {
        return handleMessageEvent(event)
                .flatMap(ExpenseUtils::parseDate)
                .map(this::processDate)
                .or(() -> handleMessageEvent(event)
                        .map(this::processTodayOption));
    }

    private List<Effect<?>> processTodayOption(String message) {
        if ("today".equalsIgnoreCase(message)) {
            StateContext stateContext = StateContext.PROVIDER.get();
            stateContext.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class)
                    .date(ClockProvider.getCurrentDate());
            stateContext.setState(ADD_DESCRIPTION);
            return List.of(ADD_DESCRIPTION.initialEffect());
        }
        return null;
    }

    private List<Effect<?>> processDate(Date date) {
        StateContext stateContext = StateContext.PROVIDER.get();
        stateContext.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class)
                .date(date);
        stateContext.setState(ADD_DESCRIPTION);
        return List.of(ADD_DESCRIPTION.initialEffect());
    }

    @Override
    public State state() {
        return ExpenseStates.ADD_DATE;
    }
}
