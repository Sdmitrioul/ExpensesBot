package com.dskroba.statemachine.handlers.group;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.groups.Group;
import com.dskroba.groups.GroupService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandler;

import java.util.List;
import java.util.Optional;

public abstract class GroupSelectionHandler extends AbstractHandler {
    private final GroupService groupService;

    protected GroupSelectionHandler(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    protected final Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(GroupSelectionHandler::stripNameOption)
                .flatMap(this::getGroupByName)
                .map(this::acceptGroup);
    }

    protected abstract List<Effect<?>> acceptGroup(Group group);

    private Optional<Group> getGroupByName(String groupName) {
        return groupService.getPrincipalGroups(PrincipalContext.principalId())
                .stream()
                .filter(g -> g.name().equalsIgnoreCase(groupName))
                .findFirst();
    }

    private static String stripNameOption(String s) {
        if (s.startsWith("[") && s.endsWith("]")) {
            return s.substring(1, s.length() - 1).trim();
        }
        return s;
    }
}
