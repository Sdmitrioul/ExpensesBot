package com.dskroba.telegram.machine.state;

import com.dskroba.notion.NotionFacade;
import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;

import java.util.List;

public class AddExpense extends AbstractState {
    public AddExpense(UserContext userContext, NotionFacade notionFacade) {
        super("Add Expense", userContext, notionFacade);
    }

    @Override
    public List<Action> getAllowedActions() {
        return List.of();
    }
}
