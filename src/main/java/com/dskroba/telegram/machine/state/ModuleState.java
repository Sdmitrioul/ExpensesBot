package com.dskroba.telegram.machine.state;

import com.dskroba.notion.NotionFacade;
import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.State;

import java.util.List;
import java.util.function.BiFunction;

public class ModuleState extends AbstractState {
    ModuleState(UserContext userContext, NotionFacade notionFacade) {
        super("module", userContext, notionFacade);
    }

    @Override
    public List<Action> getAllowedActions() {
        return List.of(ModuleActions.values());
    }

    public enum ModuleActions implements Action {
        SHOW_EXPENSES(ShowState::new, "Show expenses"),
        ADD_EXPENSE(AddExpense::new, "Add expense"),
        ;

        private final BiFunction<UserContext, NotionFacade, State> nextState;
        private final String description;

        ModuleActions(BiFunction<UserContext, NotionFacade, State> nextState, String description) {
            this.nextState = nextState;
            this.description = description;
        }

        @Override
        public State execute(State previous) {
            if (previous instanceof ModuleState moduleState) {
                return nextState.apply(moduleState.context, moduleState.notionFacade);
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
