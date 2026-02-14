package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.StateContext;
import com.dskroba.base.ClockProvider;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.expenses.ExpenseUtils;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.StateRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.ExpensesStatesDescriptors.*;
import static com.dskroba.type.SelectOption.SAVE_OPTION;
import static com.dskroba.type.SelectOption.fullRowOption;

@Component
public class AddDateHandler extends SaveStateHandler {
    @Autowired
    protected AddDateHandler(ExpenseRepository repository) {
        super(repository, StateRecord.cancelable(ADD_DATE, SelectEffect.withCancelOption(
                """
                        Add custom date, if needed
                        Format: dd.MM.YYYY""", List.of(fullRowOption("Today", 10), SAVE_OPTION))));
    }

    @Override
    protected Optional<Effect> tryUpdateExpense(Event event) {
        return handleMessageEvent(event)
                .flatMap(ExpenseUtils::parseDate)
                .map(this::processDate)
                .or(() -> handleMessageEvent(event)
                        .map(this::processTodayOption));
    }

    private Effect processTodayOption(String message) {
        if ("today".equalsIgnoreCase(message)) {
            StateContext stateContext = StateContext.PROVIDER.get();
            stateContext.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class)
                    .date(ClockProvider.getCurrentDate());
            return moveToState(ADD_DESCRIPTION);
        }
        return null;
    }

    private Effect processDate(Date date) {
        StateContext stateContext = StateContext.PROVIDER.get();
        stateContext.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class)
                .date(date);
        return moveToState(ADD_DESCRIPTION);
    }
}
