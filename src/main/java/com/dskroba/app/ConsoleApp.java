package com.dskroba.app;

import com.dskroba.base.exception.CustomException;
import com.dskroba.notion.NotionContext;
import com.dskroba.notion.NotionFacade;
import com.dskroba.type.DateFilter;
import com.dskroba.type.Expense;
import com.dskroba.type.ExpenseTag;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

public class ConsoleApp {
    private static final String AVAILABLE_TIME_FILTERS = Arrays.stream(DateFilter.values())
            .map(Enum::name)
            .collect(Collectors.joining(", ", "{", "}"));
    private static final String AVAILABLE_TAGS = Arrays.stream(ExpenseTag.values())
            .map(Enum::name)
            .collect(Collectors.joining(", ", "{", "}"));

    public static void main(String[] args) {
        Clock clock = Clock.systemUTC();
        try (Scanner scanner = new Scanner(System.in); NotionContext context = new NotionContext(clock)) {
            NotionFacade notionFacade = context.getFacade();
            while (true) {
                System.out.println("Supported commands: ");
                System.out.println("1. Add expense");
                System.out.println("2. Show expenses");
                System.out.println("Please insert a command index or 'exit' to exit command line interface");
                System.out.print("> ");
                String input = scanner.nextLine();
                if ("exit".equals(input)) {
                    return;
                }
                try {
                    if ("1".equals(input)) {
                        System.out.print("> Amount: ");
                        int amount = Integer.parseInt(scanner.nextLine());
                        System.out.print("> Tag " + AVAILABLE_TAGS + ": ");
                        ExpenseTag tag = parseEnum(ExpenseTag.class, scanner.nextLine());
                        System.out.print("> Note: ");
                        String note = scanner.nextLine();
                        Expense expense = new Expense(amount, Date.from(clock.instant()), note, List.of(tag));
                        System.out.println(notionFacade.insertExpense(expense) ? "SUCCESS" : "FAILURE");
                    } else if ("2".equals(input)) {
                        System.out.println("Specify interval");
                        System.out.println("Available intervals: " + String.join(", ", AVAILABLE_TIME_FILTERS));
                        System.out.print("> ");
                        DateFilter dateFilter = parseEnum(DateFilter.class, scanner.nextLine());
                        notionFacade.getExpenses(dateFilter.getTimeIntervals(clock)).forEach(System.out::println);
                    } else {
                        System.out.println("Unknown command: " + input);
                    }
                } catch (CustomException e) {
                    System.out.println("Error" + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String input) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(constant -> constant.name().equals(input.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new CustomException("Unknown enum value: " + input));
    }
}
