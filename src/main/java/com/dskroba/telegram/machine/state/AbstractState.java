package com.dskroba.telegram.machine.state;

import com.dskroba.notion.NotionFacade;
import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.State;
import com.dskroba.telegram.machine.StateMachineException;

public abstract class AbstractState implements State {
    private final String name;
    protected final UserContext context;
    protected final NotionFacade notionFacade;

    protected AbstractState(String name, UserContext context, NotionFacade notionFacade) {
        this.name = name;
        this.context = context;
        this.notionFacade = notionFacade;
    }

    @Override
    public State applyAction(Action action) {
        if (!getAllowedActions().contains(action)) {
            throw new StateMachineException(
                    "Not supported transition from " + this + " by action \"" + action.getDescription() + "\"");
        }
        return action.execute(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
