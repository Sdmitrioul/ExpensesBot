package com.dskroba.command;

import com.dskroba.base.ClockProvider;
import com.dskroba.base.exception.CustomException;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.type.Currency;
import com.dskroba.type.DateFilter;
import com.dskroba.expenses.Expense;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ShowExpensesCommand implements ConsoleCommand {
    private static final Logger LOGGER = LogManager.getLogger(ShowExpensesCommand.class);

    private static final String AVAILABLE_TIME_FILTERS = Arrays.stream(DateFilter.values())
            .map(Enum::name)
            .collect(Collectors.joining(", ", "{", "}"));

    private final Scanner scanner;
    private final ExpenseRepository repository;

    public ShowExpensesCommand(Scanner scanner, ExpenseRepository repository) {
        this.scanner = scanner;
        this.repository = repository;
    }

    @Override
    public String getId() {
        return "2";
    }

    @Override
    public String getName() {
        return "Show Expenses";
    }

    @Override
    public void execute() {
        System.out.println("\n--- Show Expenses ---");
        System.out.println("Available intervals: " + AVAILABLE_TIME_FILTERS);
        System.out.print("Select interval: ");

        DateFilter dateFilter = parseEnum(DateFilter.class, scanner.nextLine());

        System.out.println("\n--- Expenses for " + dateFilter.name() + " ---");
        List<Expense> expenses = repository.getExpenses(dateFilter.getTimeframe(ClockProvider.getClock()), null, Currency.EUR);

        if (expenses.isEmpty()) {
            System.out.println("No expenses found for the selected period.");
        } else {
            expenses.forEach(expense -> System.out.println("💰 " + expense));
            System.out.println("\nTotal expenses: " + expenses.size());
        }

        LOGGER.info("Retrieved {} expenses for filter: {}", expenses.size(), dateFilter);
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String input) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(constant -> constant.name().equals(input.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new CustomException("Unknown enum value: " + input));
    }
}