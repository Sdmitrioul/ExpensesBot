package com.dskroba.telegram.machine.state;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.State;

import java.util.List;
import java.util.function.Function;

public class ModuleState extends AbstractState {
    ModuleState(UserContext userContext) {
        super("module", userContext);
    }

    @Override
    public List<Action> getAllowedActions() {
        return List.of(ModuleActions.values());
    }

    public enum ModuleActions implements Action {
        SHOW_EXPENSES(ShowState::new, "Show expenses"),
        ADD_EXPENSE(AddExpenseTagState::new, "Add expense"),
        ;

        private final Function<UserContext, State> nextState;
        private final String description;

        ModuleActions(Function<UserContext, State> nextState, String description) {
            this.nextState = nextState;
            this.description = description;
        }

        @Override
        public State execute(State previous) {
            if (previous instanceof ModuleState moduleState) {
                return nextState.apply(moduleState.context);
            }
            return previous;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
