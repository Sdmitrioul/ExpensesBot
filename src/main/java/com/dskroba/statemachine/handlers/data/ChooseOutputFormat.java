package com.dskroba.statemachine.handlers.data;

import com.dskroba.actor.Context;
import com.dskroba.base.ClockProvider;
import com.dskroba.base.Utils;
import com.dskroba.base.formatter.CsvFormatter;
import com.dskroba.base.formatter.ExcelFormatter;
import com.dskroba.base.formatter.Formatter;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseFieldSelectors;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.FileEffect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.ContextAccessor;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.descriptors.DataStatesDescriptors;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.ContentType;
import com.dskroba.type.SelectOption;
import com.dskroba.type.Timeframe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.DataStatesDescriptors.TIMEFRAME_ATTRIBUTE_NAME;

@Component
public class ChooseOutputFormat extends AbstractHandlerWithFactory {
    private static final State CHOOSE_OUTPUT_FORMAT_STATE_INSTANCE = State.cancelable(
            DataStatesDescriptors.CHOOSE_OUTPUT_FORMAT,
            SelectEffect.withCancelOption("Select output file format", List.of(
                    SelectOption.base("Csv"),
                    SelectOption.base("Xlsx"))));
    private final CsvFormatter csvFormatter = new CsvFormatter(",");
    private final ExcelFormatter excelFormatter = new ExcelFormatter();
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ChooseOutputFormat(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(this::parseFormatter)
                .map(this::exportData);
    }

    private Effect exportData(Formatter formatter) {
        Context stateContext = ContextAccessor.context();
        Timeframe timeframe = stateContext.getAttribute(TIMEFRAME_ATTRIBUTE_NAME, Timeframe.class);
        List<Expense> expenses = expenseRepository.getExpenses(timeframe,
                ContextAccessor.principalCurrency(),
                ContextAccessor.principalStorage());
        byte[] data = formatter.formatOutput(ExpenseFieldSelectors.BASIC_SELECTORS, expenses);
        ContentType contentType = formatter.contentType();
        String filename = "exported_expenses_" + Utils.datePrinter(ClockProvider.getCurrentDate()) + contentType.filenameExtension();
        FileEffect effect = FileEffect.of("Exported expenses.", filename, contentType, data);
        return effect.composite(switchToMenu());
    }

    private Formatter parseFormatter(String message) {
        return switch (message.toLowerCase()) {
            case "csv" -> csvFormatter;
            case "xlsx" -> excelFormatter;
            default -> null;
        };
    }

    @Override
    public State createState() {
        return CHOOSE_OUTPUT_FORMAT_STATE_INSTANCE;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return DataStatesDescriptors.CHOOSE_OUTPUT_FORMAT;
    }
}
