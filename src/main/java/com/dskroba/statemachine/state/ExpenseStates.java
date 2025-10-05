package com.dskroba.statemachine.state;

import com.dskroba.expenses.ExpenseTag;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.type.SelectOption;

import java.util.Arrays;
import java.util.List;

import static com.dskroba.type.SelectOption.SAVE_OPTION;
import static com.dskroba.type.SelectOption.fullRowOption;

public enum ExpenseStates implements State {
    CHOOSE_TAG(SelectEffect.withCancelOption("Choose expense tag: ",
            Arrays.stream(ExpenseTag.values())
                    .map(ExpenseTag::optionName)
                    .map(SelectOption::base)
                    .toList())),
    ADD_AMOUNT(new MessageEffect("Enter expense amount: ")),
    ADD_DATE(SelectEffect.withCancelOption("Add custom date, if needed\nFormat: dd.MM.YYYY",
            List.of(fullRowOption("Today", 10), SAVE_OPTION))),
    ADD_DESCRIPTION(SelectEffect.withCancelOption("Add description, if needed",
            List.of(SAVE_OPTION))),
    SAVE_EXPENSE(SelectEffect.withCancelOption("Save or Discard?",
            List.of(SAVE_OPTION)));

    public static final String EXPENSE_ATTRIBUTE_NAME = "$expense_builder";

    private final Effect<?> effect;


    ExpenseStates(Effect<?> effect) {
        this.effect = effect;
    }

    @Override
    public Effect<?> initialEffect() {
        return effect;
    }
}
