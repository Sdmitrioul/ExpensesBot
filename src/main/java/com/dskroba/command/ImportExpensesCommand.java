package com.dskroba.command;

import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.expenses.ExpenseTag;
import com.dskroba.type.Currency;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

public class ImportExpensesCommand implements ConsoleCommand {
    private final Scanner scanner;
    private final ExpenseRepository repository;

    public ImportExpensesCommand(Scanner scanner, ExpenseRepository repository) {
        this.scanner = scanner;
        this.repository = repository;
    }

    @Override
    public String getId() {
        return "3";
    }

    @Override
    public String getName() {
        return "Import Expenses";
    }

    @Override
    public void execute() {
        System.out.println("\n--- Import Expenses ---");
        System.out.print("Filename: ");
        String filename = scanner.nextLine();
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            System.out.println("File not found: " + filename);
            return;
        }
        try (var stream = Files.lines(path)) {
            Stream.ofAll(stream.skip(1))
                    .grouped(1000)
                    .forEach(group -> repository.insertExpense(group.map(this::parseLine).asJava()));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename + "\n" + e.getMessage());
        }
        System.out.println("Import complete");
    }

    private Expense parseLine(@NotNull String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        double amount = Double.parseDouble(parts[2].replace("€", ""));
        String dateStr = parts[5].replace("\"", "");
        LocalDate localDate = parseDate(dateStr);
        Date date = Date.from(localDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
        String description = parts[3].replace("\"", "");
        ExpenseTag expenseTag = mapToExpenseTag(parts[0]);
        if (expenseTag == ExpenseTag.MISCELLANEOUS) {
            System.out.println("Unrecognized tag: " + parts[0] + ". Using default tag: " + expenseTag);
        }
        return new Expense(null, amount, -1L, date, description, expenseTag, Currency.EUR);
    }

    private static LocalDate parseDate(String dateStr) {
        String cleanDateStr = dateStr.replaceAll(" \\(.*\\)", "");

        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("MMMM d, yyyy"),
                DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a"),
                DateTimeFormatter.ofPattern("MMMM d, yyyy H:mm")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                if (formatter.toString().contains("h:mm") || formatter.toString().contains("H:mm")) {
                    return java.time.LocalDateTime.parse(cleanDateStr, formatter).toLocalDate();
                } else {
                    return LocalDate.parse(cleanDateStr, formatter);
                }
            } catch (java.time.format.DateTimeParseException e) {
                // Try next format
            }
        }

        throw new IllegalArgumentException("Unable to parse date: " + dateStr);
    }

    private static ExpenseTag mapToExpenseTag(String tagStr) {
        return switch (tagStr.toUpperCase()) {
            case "GROCERIES" -> ExpenseTag.FOOD;
            case "BILLS" -> ExpenseTag.BILLS;
            case "TRANSPORT" -> ExpenseTag.TRANSPORT;
            case "HEALTHCARE", "HEALTH" -> ExpenseTag.HEALTHCARE;
            case "ANIMAL", "DOG" -> ExpenseTag.ANIMAL;
            case "ENTERTAINMENT" -> ExpenseTag.ENTERTAINMENT;
            case "CLOTHES" -> ExpenseTag.CLOTHES;
            case "BEAUTY" -> ExpenseTag.BEAUTY;
            case "HOME" -> ExpenseTag.HOME;
            case "SPORT" -> ExpenseTag.SPORT;
            case "SAVINGS" -> ExpenseTag.SAVINGS;
            case "DEBT" -> ExpenseTag.DEBT;
            case "INSURANCE" -> ExpenseTag.INSURANCE;
            case "CAFE" -> ExpenseTag.CAFE;
            default -> ExpenseTag.MISCELLANEOUS;
        };
    }
}