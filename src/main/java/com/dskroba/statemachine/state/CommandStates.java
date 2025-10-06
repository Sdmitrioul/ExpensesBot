package com.dskroba.statemachine.state;

import com.dskroba.expenses.ExpenseTag;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.type.Currency;
import com.dskroba.type.SelectOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CommandStates implements State {
    MENU(SelectEffect.of("Choose menu operation", List.of(
            SelectOption.base("Expenses"),
            SelectOption.base("Statistic"),
            SelectOption.base("Data"))), "Provides bot menu.", false),
    IMPORT_DATA(SelectEffect.withCancelOption("""
                    Please, send csv file with data.
                    Delimiter is <,>.
                    File must contains next columns: [amount, tag, date, description].
                    File must contain header.
                    Next columns are required: [amount, tag, date].
                    Date must be in format: dd.MM.yyyy
                    Tag column must be one of:\s""" + Arrays.stream(ExpenseTag.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", ", "[", "]")),
            List.of()),
            "Allow import expenses from csv file."),
    EXPORT_DATA(SelectEffect.withCancelOption("Select historical period.", List.of(
            SelectOption.base("Current Month"),
            SelectOption.base("Previous Month"),
            SelectOption.base("All history"))), "Allow export expenses."),
    SET_ACTIVE_CURRENCY(SelectEffect.withCancelOption("Select active currency.",
            Arrays.stream(Currency.values())
                    .map(Currency::name)
                    .map(SelectOption::base)
                    .toList()), "Allow setup of active account currency.");

    private final Effect<?> effect;
    private final String description;
    private final boolean cancelable;

    CommandStates(Effect<?> effect, String description) {
        this(effect, description, true);
    }

    CommandStates(Effect<?> effect, String description, boolean cancelable) {
        this.effect = effect;
        this.description = description;
        this.cancelable = cancelable;
    }

    @Override
    public Effect<?> initialEffect() {
        return effect;
    }

    @Override
    public boolean isCancellable() {
        return cancelable;
    }

    public String code() {
        return "/" + name().toLowerCase();
    }

    public String getDescription() {
        return description;
    }
}
