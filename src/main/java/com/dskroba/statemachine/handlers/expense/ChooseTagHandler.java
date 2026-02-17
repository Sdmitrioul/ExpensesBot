package com.dskroba.statemachine.handlers.expense;

import com.dskroba.base.ClockProvider;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseType;
import com.dskroba.expenses.ExpenseUtils;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.ContextAccessor;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateContext;
import com.dskroba.statemachine.state.descriptors.ExpensesStatesDescriptors;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.SelectOption;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import static com.dskroba.statemachine.state.descriptors.ExpensesStatesDescriptors.*;

@Component
public class ChooseTagHandler extends AbstractHandlerWithFactory {
    private static final State CHOOSE_TAG_STATE_INSTANCE = State.cancelable(
            ExpensesStatesDescriptors.CHOOSE_TAG,
            SelectEffect.withCancelOption("Choose expense tag: ",
                    Arrays.stream(ExpenseType.values())
                            .map(ExpenseType::optionName)
                            .map(SelectOption::base)
                            .toList()));

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(String::toUpperCase)
                .flatMap(ExpenseUtils::parseTag)
                .map(this::processTag);
    }

    private Effect processTag(ExpenseType tag) {
        return moveToState(ADD_AMOUNT, setExpenseBuilder(tag));
    }

    private static Consumer<StateContext> setExpenseBuilder(ExpenseType tag) {
        return context -> context
                .setAttribute(EXPENSE_ATTRIBUTE_NAME, new Expense.Builder()
                        .type(tag)
                        .issuer(ContextAccessor.principalTelegramHandle())
                        .date(ClockProvider.getCurrentDate()));
    }

    @Override
    public State createState() {
        return CHOOSE_TAG_STATE_INSTANCE;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return CHOOSE_TAG;
    }
}
