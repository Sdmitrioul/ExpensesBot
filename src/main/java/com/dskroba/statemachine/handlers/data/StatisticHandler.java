package com.dskroba.statemachine.handlers.data;

import com.dskroba.FP;
import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.base.ClockProvider;
import com.dskroba.base.exception.CustomException;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.services.PlottingService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.FileEffect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandler;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StatisticStates;
import com.dskroba.type.Currency;
import com.dskroba.type.DateFilter;
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

import static com.dskroba.statemachine.state.CommandStates.MENU;
import static com.dskroba.type.ContentType.PNG;
import static com.dskroba.type.DateFilter.*;

@Component
public class StatisticHandler extends AbstractHandler {
    private final ExpenseRepository expenseRepository;
    private final PlottingService plottingService;

    @Autowired
    public StatisticHandler(ExpenseRepository expenseRepository, PlottingService plottingService) {
        this.expenseRepository = expenseRepository;
        this.plottingService = plottingService;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(this::parseDateFilter)
                .map(this::createAndSendGraphic)
                .or(() -> handleMessageEvent(event)
                        .map(this::monthComparison));
    }

    private List<Effect<?>> monthComparison(String message) {
        if (!"Compare months".equalsIgnoreCase(message)) {
            return null;
        }
        Long issuerId = PrincipalContext.issuerId();
        Currency currency = PrincipalContext.currency();
        Clock clock = ClockProvider.getClock();
        Timeframe monthTimeframe = MONTH.getTimeframe(clock);
        var currentMonth = getGroupedExpenses(monthTimeframe, issuerId, currency);
        Timeframe previousMonthTimeframe = PREVIOUS_MONTH.getTimeframe(clock);
        var previousMonth = getGroupedExpenses(previousMonthTimeframe, issuerId, currency);
        if (currentMonth.isEmpty() && previousMonth.isEmpty()) {
            return sendEffect(new MessageEffect("No expenses found for the selected period."));
        }
        try {
            Month month = LocalDate.ofInstant(Date.from(previousMonthTimeframe.from()).toInstant(), ClockProvider.getZoneId()).getMonth();
            String moths = month.name() + " - " + month.plus(1).name();
            return sendEffect(createChartEffect(plottingService.barChart("Expenses comparison for: " + moths, previousMonth, currentMonth, currency)));
        } catch (CustomException e) {
            return sendEffect(new MessageEffect("Error during generating chart!"));
        }
    }

    private List<Effect<?>> sendEffect(Effect<?> effect) {
        StateContext.PROVIDER.get().setState(MENU);
        return List.of(effect, MENU.initialEffect());
    }

    private static FileEffect createChartEffect(byte[] bytes) {
        return FileEffect.of("", "chart.png", PNG, bytes);
    }

    private List<Effect<?>> createAndSendGraphic(DateFilter dateFilter) {
        Timeframe timeframe = dateFilter.getTimeframe(ClockProvider.getClock());
        Long issuerId = PrincipalContext.issuerId();
        Currency currency = PrincipalContext.currency();
        Map<String, Double> groupedExpenses = getGroupedExpenses(timeframe, issuerId, currency);
        if (groupedExpenses.isEmpty()) {
            return sendEffect(new MessageEffect("No expenses found for the selected period."));
        }
        try {
            return sendEffect(createChartEffect(plottingService.pieChart("Expenses " + timeframe.toPrettyString(), groupedExpenses, currency)));
        } catch (CustomException e) {
            return sendEffect(new MessageEffect("Error during generating chart!"));
        }
    }

    @NotNull
    private Map<String, Double> getGroupedExpenses(Timeframe timeframe, Long issuerId, Currency currency) {
        return expenseRepository.getExpenses(timeframe,
                        issuerId,
                        currency)
                .stream()
                .collect(Collectors.groupingBy(FP.compose(Expense::expenseTag, Enum::name),
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
    public State state() {
        return StatisticStates.STATISTIC_STATE;
    }
}
