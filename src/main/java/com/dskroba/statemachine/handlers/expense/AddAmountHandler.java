package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.StateContext;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseUtils;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateRecord;
import com.dskroba.statemachine.state.descriptors.ExpensesStatesDescriptors;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.ExpensesStatesDescriptors.*;

@Component
public class AddAmountHandler extends AbstractHandlerWithFactory {
    public static final StateRecord ADD_AMOUNT_STATE_INSTANCE = StateRecord.cancelable(
            ExpensesStatesDescriptors.ADD_AMOUNT, new MessageEffect("Enter expense amount: "));

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(amount -> amount.replace(",", "."))
                .flatMap(ExpenseUtils::parseAmount)
                .map(this::processAmount);
    }

    private Effect processAmount(Double amount) {
        StateContext stateContext = StateContext.PROVIDER.get();
        stateContext.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class)
                .amount(amount);
        return moveToState(ADD_DATE);
    }

    @Override
    public State createState() {
        return ADD_AMOUNT_STATE_INSTANCE;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return ADD_AMOUNT;
    }
}