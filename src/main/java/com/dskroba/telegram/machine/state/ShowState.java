package com.dskroba.telegram.machine.state;

import com.dskroba.base.TablePrinter;
import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.State;
import com.dskroba.type.DateFilter;
import com.dskroba.type.Expense;
import com.pengrad.telegrambot.model.request.ParseMode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dskroba.base.TablePrinter.Format.ELIMINATE_EMPTY_COLUMNS;
import static com.dskroba.type.Expense.AGGREGATED_HEADERS;
import static com.dskroba.type.Expense.SHORT_HEADERS;

public class ShowState extends AbstractState {
    ShowState(UserContext userContext) {
        super("Show state", userContext);
    }

    @Override
    public List<Action> getAllowedActions() {
        return List.of(ShowActions.values());
    }

    @Override
    public Optional<String> message() {
        return Optional.of("Choose report period");
    }

    public enum ShowActions implements Action {
        DAY_EXPENSES(DateFilter.TODAY, "Day expenses"),
        WEEK_EXPENSES(DateFilter.WEEK, "Week expenses"),
        MONTH_EXPENSES(DateFilter.MONTH, "Month expenses"),
        ;

        private final DateFilter dateFilter;
        private final String description;

        ShowActions(DateFilter dateFilter, String description) {
            this.dateFilter = dateFilter;
            this.description = description;
        }

        @Override
        public State execute(State previous) {
            if (previous instanceof ShowState showState) {
                UserContext userContext = showState.context;
                List<Expense> expenses = userContext.getUserExpenses(dateFilter);
                if (expenses.isEmpty()) {
                    userContext.makeResponse("Nothing to show");
                    return new ModuleState(userContext);
                }
                String[] headers = this == MONTH_EXPENSES ? AGGREGATED_HEADERS : SHORT_HEADERS;
                TablePrinter printer = new TablePrinter(ELIMINATE_EMPTY_COLUMNS, headers);
                if (this != MONTH_EXPENSES) {
                    expenses.forEach(expense -> printer.addRow(
                            expense.getFistTagName(""),
                            String.format("%.2f", expense.getAmount()),
                            expense.getNote()));
                } else {
                    expenses.stream()
                            .collect(Collectors.toMap(Expense::getFistTag, Expense::getAmount, Double::sum))
                            .forEach((key, value) -> printer.addRow(key, String.format("%.2f", value)));
                }
                String text = "<pre>" + printer.print() + "</pre>";
                userContext.makeResponse(text, message -> message.parseMode(ParseMode.HTML));
                return new ModuleState(userContext);
            }
            return previous;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
