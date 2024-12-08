package com.dskroba.telegram.machine.state;

import com.dskroba.notion.NotionFacade;
import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.State;
import com.dskroba.type.DateFilter;
import com.dskroba.type.Expense;
import com.dskroba.type.ExpenseTag;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.util.function.Predicate.not;

public class ShowState extends AbstractState {
    ShowState(UserContext userContext, NotionFacade notionFacade) {
        super("Show state", userContext, notionFacade);
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
                List<Expense> expenses = showState.notionFacade
                        .getExpenses(dateFilter.getTimeIntervals(userContext.getClock()));
                int maxTagSize = max(expenses.stream()
                        .map(Expense::getTag)
                        .filter(not(List::isEmpty))
                        .map(List::getFirst)
                        .map(ExpenseTag::name)
                        .mapToInt(String::length)
                        .max()
                        .orElse(3), 3);
                int descriptionSize = Math.max(expenses.stream()
                        .map(Expense::getNote)
                        .mapToInt(String::length)
                        .max()
                        .orElse(11), 11);
                String text = expenses
                        .stream()
                        .map(expense -> expense.toShortString(maxTagSize, descriptionSize))
                        .collect(Collectors.joining("\n",
                                String.format("<pre>| %-" + maxTagSize + "s | %-8s | %-" + descriptionSize + "s |", "Tag", "Amount", "Description") +
                                        "\n" +
                                        "-".repeat(maxTagSize + descriptionSize + 18) +
                                        "\n",
                                "</pre>"));
                SendMessage message = new SendMessage(userContext.getChatId(), text);
                message.parseMode(ParseMode.HTML);
                userContext.getExecutorService().execute(() -> userContext.getBot().execute(message));
                return new ModuleState(userContext, showState.notionFacade);
            }
            return previous;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
