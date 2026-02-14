package com.dskroba.statemachine.handlers.command;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.base.csv.CsvParser;
import com.dskroba.base.csv.IncorrectRowEntry;
import com.dskroba.base.exception.CustomException;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseCsvParser;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.expenses.ExpenseType;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.FileEffect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.events.FileUploadEvent;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateRecord;
import com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.ContentType;
import com.dskroba.type.FileData;
import com.dskroba.type.FileWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;

import static com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors.IMPORT_DATA;

@Component
public class ImportDataHandler extends AbstractHandlerWithFactory {
    private static final StateRecord IMPORT_DATA_STATE_INSTANCE = StateRecord.cancelable(CommandStatesDescriptors.IMPORT_DATA, SelectEffect.withCancelOption("""
                    Please, send csv file with data.
                    Delimiter is <,>.
                    File must contains next columns: [amount, tag, date, description].
                    File must contain header.
                    Next columns are required: [amount, tag, date].
                    Date must be in format: dd.MM.yyyy
                    Tag column must be one of:\s""" + Arrays.stream(ExpenseType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", ", "[", "]")),
            List.of()));

    private final CsvParser<Expense.Builder> csvParser = new ExpenseCsvParser(",");
    private final ExpenseRepository repository;

    @Autowired
    public ImportDataHandler(ExpenseRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return Optional.of(event)
                .filter(e -> e instanceof FileUploadEvent)
                .map(e -> (FileUploadEvent) e)
                .map(this::tryParseFile);
    }

    private Effect tryParseFile(FileUploadEvent fileUploadEvent) {
        if (invalidFileFormat(fileUploadEvent)) {
            return MessageEffect.of("Unsupported file format!").composite(StateContext.getCurrentState().initialEffect());
        }
        List<Expense.Builder> expenses = new ArrayList<>();
        List<IncorrectRowEntry> errors = new ArrayList<>();
        try {
            String header = csvParser.parse(new ByteArrayInputStream(fileUploadEvent.payload().fileData().data()),
                    expense -> expenses.add(expense.value()),
                    errors::add);
            Effect effect;
            if (!errors.isEmpty()) {
                effect = convertErrorEntries(header, errors)
                        .composite(saveExpenses(expenses));
            } else {
                effect = saveExpenses(expenses);
            }
            return effect.composite(switchToMenu());
        } catch (CustomException e) {
            return MessageEffect.of(e.getMessage()).composite(StateContext.getCurrentState().initialEffect());
        }
    }

    private Effect convertErrorEntries(String header, List<IncorrectRowEntry> errors) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter output = new OutputStreamWriter(outputStream)) {
            output.append("original_row_number,exception_cause,").append(header).append("\n");
            for (IncorrectRowEntry incorrectRowEntry : errors) {
                output.append(Long.toString(incorrectRowEntry.rowNumber()))
                        .append(",\"")
                        .append(incorrectRowEntry.causeMessage())
                        .append("\",")
                        .append(incorrectRowEntry.originalEntry())
                        .append("\n");
            }
        } catch (IOException e) {
            log.error("Shouldn't happen: ", e);
            return new MessageEffect("There is problem with providing entries with errors!");
        }
        return new FileEffect(new FileWithMessage(
                new FileData("unparsed_entries.csv", ContentType.CSV, outputStream.toByteArray()),
                """
                        We were unable to insert entries in this file
                        Please, fix problems marked provided in second column
                        """));
    }

    private Effect saveExpenses(List<Expense.Builder> expenses) {
        if (expenses.isEmpty()) {
            return new MessageEffect("No expenses were saved.");
        }
        var principal = PrincipalContext.PROVIDER.get().getPrincipal();
        try {
            repository.insertExpense(expenses.stream()
                    .map(expense -> expense.issuerId(principal.key().telegramId()))
                    .map(expense -> {
                        if (expense.currency() == null) {
                            return expense.currency(principal.context().activeCurrency());
                        }
                        return expense;
                    })
                    .map(Expense.Builder::build)
                    .toList(), PrincipalContext.table());
        } catch (CustomException e) {
            log.error("Exception during expenses saving.", e);
            return new MessageEffect("""
                    Problem appeared during saving of expenses.
                    Please Try again""");
        }
        return new MessageEffect("Successfully saved " + expenses.size() + " expenses!");
    }

    private static boolean invalidFileFormat(FileUploadEvent fileUploadEvent) {
        return !Objects.equals(fileUploadEvent.payload().fileData().contentType().value(), ContentType.CSV.value());
    }

    @Override
    public State createState() {
        return IMPORT_DATA_STATE_INSTANCE;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return IMPORT_DATA;
    }
}
