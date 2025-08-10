package com.dskroba.telegram.machine.state;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.*;
import com.dskroba.type.Expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class AddExpenseDateState extends ExpenseSavableState {
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public final List<Action> actions;

    public AddExpenseDateState(UserContext context, Expense.Builder builder) {
        super("Set custom expense date", context, builder);
        this.actions = List.of(new SaveExpenseAction(expenseBuilder, StateFactory.getStartState(context)),
                CancelAction.create(context));
    }

    @Override
    public Optional<String> message() {
        return Optional.of("Set expense date if needed\nP.S. date format is " + DATE_FORMAT);
    }

    @Override
    public List<Action> getAllowedActions() {
        return actions;
    }

    @Override
    protected Action parseCustomAction(String text) {
        try {
            LocalDate localDate = LocalDate.parse(text, FORMATTER);
            Date date = Date.from(localDate.atStartOfDay(context.getClock().getZone()).toInstant());
            return new AddExpenseDateAction(expenseBuilder.date(date));
        } catch (DateTimeParseException e) {
            return new TryAgainAction("Incorrect date format");
        }
    }

    private record AddExpenseDateAction(Expense.Builder expenseBuilder) implements Action {
        @Override
        public State execute(State previous) {
            if (previous instanceof AddExpenseDateState setCustomExpenseDateState) {
                setCustomExpenseDateState.save(expenseBuilder.build());
                return StateFactory.getStartState(setCustomExpenseDateState.context);
            }
            return previous;
        }

        @Override
        public boolean isHidden() {
            return true;
        }

        @Override
        public String getDescription() {
            return "Add date";
        }
    }
}
