package com.dskroba.telegram.machine.state;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.CancelAction;
import com.dskroba.telegram.machine.State;
import com.dskroba.type.ExpenseTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AddExpenseTagState extends ExpenseBuilderState {
    private final List<Action> actions;

    public AddExpenseTagState(UserContext userContext) {
        super("Add Expense", userContext);
        actions = new ArrayList<>();
        Arrays.stream(ExpenseTag.values())
                .map(AddTagAction::new)
                .forEach(actions::add);
        actions.add(CancelAction.create(userContext));
    }

    @Override
    public List<Action> getAllowedActions() {
        return actions;
    }

    @Override
    public Optional<String> message() {
        return Optional.of("Choose an expense tag");
    }

    private record AddTagAction(ExpenseTag tag) implements Action {
        @Override
        public State execute(State previous) {
            if (previous instanceof AddExpenseTagState addExpense) {
                return new AddExpenseAmountState(addExpense.context, addExpense.expenseBuilder.putTag(tag));
            }
            return previous;
        }

        @Override
        public String getDescription() {
            return tag.name();
        }
    }
}
