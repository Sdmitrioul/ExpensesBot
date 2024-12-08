package com.dskroba.telegram.machine.state;

import com.dskroba.notion.NotionFacade;
import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.State;

public final class StateFactory {
    public static State getStartState(UserContext userContext, NotionFacade notionFacade) {
        return new ModuleState(userContext, notionFacade);
    }

    private StateFactory() {
    }
}
