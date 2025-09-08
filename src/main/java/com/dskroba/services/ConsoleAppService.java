package com.dskroba.services;

import com.dskroba.base.exception.CustomException;
import com.dskroba.command.AddExpenseCommand;
import com.dskroba.command.ConsoleCommand;
import com.dskroba.command.ImportExpensesCommand;
import com.dskroba.command.ShowExpensesCommand;
import com.dskroba.repository.ExpenseRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
@ConditionalOnProperty(
        name = "application.console.enabled",
        havingValue = "true"
)
public class ConsoleAppService implements CommandLineRunner {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleAppService.class);

    private final Optional<ExpenseRepository> expenseRepository;
    private final Map<String, ConsoleCommand> commands = new HashMap<>();

    @Autowired
    public ConsoleAppService(Optional<ExpenseRepository> expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public void run(String... args) {
        if (expenseRepository.isEmpty()) {
            LOGGER.error("Console app requires Notion to be enabled. Please set notion.enabled=true");
            System.err.println("Console app requires Notion to be enabled. Please set notion.enabled=true");
            return;
        }

        LOGGER.info("Starting console application");
        runConsoleInterface();
    }

    private void registerCommands(Scanner scanner) {
        ExpenseRepository repository = expenseRepository
                .orElseThrow(() -> new CustomException("Notion context is not available"));

        AddExpenseCommand addCommand = new AddExpenseCommand(scanner, repository);
        ShowExpensesCommand showCommand = new ShowExpensesCommand(scanner, repository);
        ImportExpensesCommand importCommand = new ImportExpensesCommand(scanner, repository);

        commands.put(addCommand.getId(), addCommand);
        commands.put(showCommand.getId(), showCommand);
        commands.put(importCommand.getId(), importCommand);
    }

    private void runConsoleInterface() {
        try (Scanner scanner = new Scanner(System.in)) {
            registerCommands(scanner);
            while (true) {
                printMenu();
                String input = scanner.nextLine();

                if ("exit".equals(input)) {
                    LOGGER.info("Exiting console application");
                    return;
                }

                try {
                    ConsoleCommand command = commands.get(input);
                    if (command != null) {
                        command.execute();
                    } else {
                        System.out.println("Unknown command: " + input);
                        System.out.println("Please enter '1', '2', '3', or 'exit'");
                    }
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
        for (var command : commands.values()) {
            System.out.printf("%s. %s%n", command.getId(), command.getName());
        }
        System.out.println("Type 'exit' to quit");
        System.out.println("=".repeat(50));
        System.out.print("> ");
    }
}