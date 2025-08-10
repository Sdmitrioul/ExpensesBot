package com.dskroba.services;

import com.dskroba.base.exception.CustomException;
import com.dskroba.notion.NotionContext;
import com.dskroba.notion.NotionFacade;
import com.dskroba.type.DateFilter;
import com.dskroba.type.Expense;
import com.dskroba.type.ExpenseTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(
        name = "application.console.enabled",
        havingValue = "true"
)
public class ConsoleAppService implements CommandLineRunner {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleAppService.class);

    private static final String AVAILABLE_TIME_FILTERS = Arrays.stream(DateFilter.values())
            .map(Enum::name)
            .collect(Collectors.joining(", ", "{", "}"));

    private static final String AVAILABLE_TAGS = Arrays.stream(ExpenseTag.values())
            .map(Enum::name)
            .collect(Collectors.joining(", ", "{", "}"));

    private final Clock clock;
    private final Optional<NotionContext> notionContext;

    @Autowired
    public ConsoleAppService(Clock clock, Optional<NotionContext> notionFacade) {
        this.clock = clock;
        this.notionContext = notionFacade;
    }

    @Override
    public void run(String... args) throws Exception {
        if (notionContext.isEmpty()) {
            LOGGER.error("Console app requires Notion to be enabled. Please set notion.enabled=true");
            System.err.println("Console app requires Notion to be enabled. Please set notion.enabled=true");
            return;
        }

        LOGGER.info("Starting console application");
        runConsoleInterface();
    }

    private void runConsoleInterface() {
        try (Scanner scanner = new Scanner(System.in)) {
            NotionFacade facade = notionContext.get().getFacade();

            while (true) {
                printMenu();
                String input = scanner.nextLine();

                if ("exit".equals(input)) {
                    LOGGER.info("Exiting console application");
                    return;
                }

                try {
                    handleCommand(input, scanner, facade);
                } catch (CustomException e) {
                    System.out.println("Error: " + e.getMessage());
                    LOGGER.warn("Command execution error: {}", e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid number format");
                    LOGGER.warn("Invalid number format: {}", e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error occurred");
                    LOGGER.error("Unexpected error in console app", e);
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Expense Tracker Console");
        System.out.println("=".repeat(50));
        System.out.println("Supported commands:");
        System.out.println("1. Add expense");
        System.out.println("2. Show expenses");
        System.out.println("Type 'exit' to quit");
        System.out.println("=".repeat(50));
        System.out.print("> ");
    }

    private void handleCommand(String input, Scanner scanner, NotionFacade facade) {
        switch (input) {
            case "1" -> handleAddExpense(scanner, facade);
            case "2" -> handleShowExpenses(scanner, facade);
            default -> {
                System.out.println("Unknown command: " + input);
                System.out.println("Please enter '1', '2', or 'exit'");
            }
        }
    }

    private void handleAddExpense(Scanner scanner, NotionFacade facade) {
        System.out.println("\n--- Add New Expense ---");

        System.out.print("Amount: ");
        int amount = Integer.parseInt(scanner.nextLine());

        System.out.print("Tag " + AVAILABLE_TAGS + ": ");
        ExpenseTag tag = parseEnum(ExpenseTag.class, scanner.nextLine());

        System.out.print("Note: ");
        String note = scanner.nextLine();

        Expense expense = new Expense(
                amount,
                Date.from(clock.instant()),
                note,
                List.of(tag)
        );

        boolean success = facade.insertExpense(expense);
        System.out.println("\nResult: " + (success ? "✅ SUCCESS" : "❌ FAILURE"));

        if (success) {
            LOGGER.info("Expense added successfully: amount={}, tag={}, note={}",
                    amount, tag, note);
        } else {
            LOGGER.warn("Failed to add expense: amount={}, tag={}, note={}",
                    amount, tag, note);
        }
    }

    private void handleShowExpenses(Scanner scanner, NotionFacade facade) {
        System.out.println("\n--- Show Expenses ---");
        System.out.println("Available intervals: " + AVAILABLE_TIME_FILTERS);
        System.out.print("Select interval: ");

        DateFilter dateFilter = parseEnum(DateFilter.class, scanner.nextLine());

        System.out.println("\n--- Expenses for " + dateFilter.name() + " ---");
        List<Expense> expenses = facade.getExpenses(dateFilter.getTimeIntervals(clock));

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
