package com.dskroba.statemachine.handlers.data;

import com.dskroba.FP;
import com.dskroba.base.ClockProvider;
import com.dskroba.base.exception.CustomException;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.google.GoogleTable;
import com.dskroba.services.PlottingService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.FileEffect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.ContextAccessor;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.statemachine.state.descriptors.StatisticStatesDescriptors;
import com.dskroba.type.Currency;
import com.dskroba.type.DateFilter;
import com.dskroba.type.SelectOption;
import com.dskroba.type.Timeframe;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dskroba.type.ContentType.PNG;
import static com.dskroba.type.DateFilter.*;

@Component
public class StatisticHandler extends AbstractHandlerWithFactory {
    private static final State CANCELABLE_STATE_INSTANCE = State.cancelable(
            StatisticStatesDescriptors.STATISTIC_STATE,
            SelectEffect.of(List.of(
                    SelectOption.base("Day", 30),
                    SelectOption.base("Week", 29),
                    SelectOption.base("Month", 28),
                    SelectOption.fullRowOption("Compare months", 27),
                    SelectOption.CANCEL_OPTION)));

    private final ExpenseRepository expenseRepository;
    private final PlottingService plottingService;

    @Autowired
    public StatisticHandler(ExpenseRepository expenseRepository, PlottingService plottingService) {
        this.expenseRepository = expenseRepository;
        this.plottingService = plottingService;
    }

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(this::parseDateFilter)
                .map(this::createAndSendGraphic)
                .or(() -> handleMessageEvent(event)
                        .map(this::monthComparison));
    }

    private Effect monthComparison(String message) {
        if (!"Compare months".equalsIgnoreCase(message)) {
            return null;
        }
        GoogleTable table = ContextAccessor.principalStorage();
        Currency currency = ContextAccessor.principalCurrency();
        Clock clock = ClockProvider.getClock();
        Timeframe monthTimeframe = MONTH.getTimeframe(clock);
        var currentMonth = getGroupedExpenses(monthTimeframe, currency, table);
        Timeframe previousMonthTimeframe = PREVIOUS_MONTH.getTimeframe(clock);
        var previousMonth = getGroupedExpenses(previousMonthTimeframe, currency, table);
        if (currentMonth.isEmpty() && previousMonth.isEmpty()) {
            return sendEffectAndSwitchToMenu(new MessageEffect("No expenses found for the selected period."));
        }
        try {
            Month month = LocalDate.ofInstant(Date.from(previousMonthTimeframe.from()).toInstant(), ClockProvider.getZoneId()).getMonth();
            String moths = month.name() + " - " + month.plus(1).name();
            return sendEffectAndSwitchToMenu(createChartEffect(plottingService.barChart("Expenses comparison for: " + moths, previousMonth, currentMonth, currency)));
        } catch (CustomException e) {
            return sendEffectAndSwitchToMenu(new MessageEffect("Error during generating chart!"));
        }
    }

    private Effect sendEffectAndSwitchToMenu(Effect effect) {
        return effect.composite(switchToMenu());
    }

    private static FileEffect createChartEffect(byte[] bytes) {
        return FileEffect.of("", "chart.png", PNG, bytes);
    }

    private Effect createAndSendGraphic(DateFilter dateFilter) {
        Timeframe timeframe = dateFilter.getTimeframe(ClockProvider.getClock());
        GoogleTable table = ContextAccessor.principalStorage();
        Currency currency = ContextAccessor.principalCurrency();
        Map<String, Double> groupedExpenses = getGroupedExpenses(timeframe, currency, table);
        if (groupedExpenses.isEmpty()) {
            return sendEffectAndSwitchToMenu(new MessageEffect("No expenses found for the selected period."));
        }
        try {
            return sendEffectAndSwitchToMenu(createChartEffect(plottingService.pieChart("Expenses " + timeframe.toPrettyString(), groupedExpenses, currency)));
        } catch (CustomException e) {
            return sendEffectAndSwitchToMenu(new MessageEffect("Error during generating chart!"));
        }
    }

    @NotNull
    private Map<String, Double> getGroupedExpenses(Timeframe timeframe, Currency currency, GoogleTable table) {
        return expenseRepository.getExpenses(timeframe, currency, table)
                .stream()
                .collect(Collectors.groupingBy(FP.compose(Expense::expenseType, Enum::name),
                        Collectors.summingDouble(Expense::amount)));
    }

    private DateFilter parseDateFilter(String message) {
        return switch (message.toLowerCase()) {
            case "day" -> TODAY;
            case "week" -> WEEK;
            case "month" -> MONTH;
            default -> null;
        };
    }

    @Override
    public State createState() {
        return CANCELABLE_STATE_INSTANCE;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return StatisticStatesDescriptors.STATISTIC_STATE;
    }
}
