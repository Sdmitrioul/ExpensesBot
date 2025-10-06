package com.dskroba.statemachine.handlers.data;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
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
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandler;
import com.dskroba.statemachine.state.DataStates;
import com.dskroba.statemachine.state.State;
import com.dskroba.type.ContentType;
import com.dskroba.type.Timeframe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.CommandStates.MENU;
import static com.dskroba.statemachine.state.DataStates.TIMEFRAME_ATTRIBUTE_NAME;

@Component
public class ChooseOutputFormat extends AbstractHandler {
    private final CsvFormatter csvFormatter = new CsvFormatter(",");
    private final ExcelFormatter excelFormatter = new ExcelFormatter();
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ChooseOutputFormat(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(this::parseFormatter)
                .map(this::exportData);
    }

    private List<Effect<?>> exportData(Formatter formatter) {
        StateContext stateContext = StateContext.PROVIDER.get();
        Timeframe timeframe = stateContext.getAttribute(TIMEFRAME_ATTRIBUTE_NAME, Timeframe.class);
        stateContext.clearAttributes();
        Long issuerId = PrincipalContext.issuerId();
        List<Expense> expenses = expenseRepository.getExpenses(timeframe, issuerId);
        byte[] data = formatter.formatOutput(ExpenseFieldSelectors.BASIC_SELECTORS, expenses);
        ContentType contentType = formatter.contentType();
        String filename = "exported_expenses_" + Utils.datePrinter(ClockProvider.getCurrentDate()) + contentType.filenameExtension();
        FileEffect effect = FileEffect.of("Exported expenses.", filename, contentType, data);
        stateContext.setState(MENU);
        return List.of(effect, MENU.initialEffect());
    }

    private Formatter parseFormatter(String message) {
        return switch (message.toLowerCase()) {
            case "csv" -> csvFormatter;
            case "xlsx" -> excelFormatter;
            default -> null;
        };
    }

    @Override
    public State state() {
        return DataStates.CHOOSE_OUTPUT_FORMAT;
    }
}
