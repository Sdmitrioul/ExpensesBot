package com.dskroba.command;

import com.dskroba.base.ClockProvider;
import com.dskroba.base.exception.CustomException;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.expenses.ExpenseType;
import com.dskroba.type.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AddExpenseCommand implements ConsoleCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddExpenseCommand.class);

    private static final String AVAILABLE_TAGS = Arrays.stream(ExpenseType.values())
            .map(Enum::name)
            .collect(Collectors.joining(", ", "{", "}"));

    private final Scanner scanner;
    private final ExpenseRepository repository;

    public AddExpenseCommand(Scanner scanner, ExpenseRepository repository) {
        this.scanner = scanner;
        this.repository = repository;
    }

    @Override
    public String getId() {
        return "1";
    }

    @Override
    public String getName() {
        return "Add New Expense";
    }

    @Override
    public void execute() {
        System.out.println("\n--- Add New Expense ---");

        System.out.print("Amount: ");
        int amount = Integer.parseInt(scanner.nextLine());

        System.out.print("Tag " + AVAILABLE_TAGS + ": ");
        ExpenseType tag = parseEnum(ExpenseType.class, scanner.nextLine());

        System.out.print("Note: ");
        String note = scanner.nextLine();

        Expense expense = new Expense(
                null,
                tag,
                amount,
                Currency.EUR,
                note,
                -1L, -1L,
                Date.from(ClockProvider.getClock().instant())
        );

        boolean success = repository.insertExpense(expense);
        System.out.println("\nResult: " + (success ? "✅ SUCCESS" : "❌ FAILURE"));

        if (success) {
            LOGGER.info("Expense added successfully: amount={}, tag={}, note={}",
                    amount, tag, note);
        } else {
            LOGGER.warn("Failed to add expense: amount={}, tag={}, note={}",
                    amount, tag, note);
        }
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String input) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(constant -> constant.name().equals(input.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new CustomException("Unknown enum value: " + input));
    }
}