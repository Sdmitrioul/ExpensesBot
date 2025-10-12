package com.dskroba.statemachine.handlers.command;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.groups.Group;
import com.dskroba.groups.GroupService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.StateSelector;
import com.dskroba.statemachine.state.CommandStates;
import com.dskroba.statemachine.state.GroupStates;
import com.dskroba.statemachine.state.State;
import com.dskroba.type.SelectOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GroupSettingsHandler extends StateSelector {
    private static final Map<String, State> EVENT_STATE_MAP = Map.of(
    );

    private final GroupService groupService;

    @Autowired
    public GroupSettingsHandler(GroupService groupService) {
        super(EVENT_STATE_MAP);
        this.groupService = groupService;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return super.handleStateEvents(event)
                .or(() -> handleMessageEvent(event)
                        .filter("set active group"::equalsIgnoreCase)
                        .map(_ -> groupService.getPrincipalGroups(PrincipalContext.principalId()))
                        .map(this::createSelectGroupEffect));
    }

    private List<Effect<?>> createSelectGroupEffect(List<Group> groups) {
        List<SelectOption> options = new ArrayList<>();
        for (Group group : groups) {
            SelectOption option;
            if (Objects.equals(group.id(), PrincipalContext.group())) {
                option = SelectOption.base("[ " + group.name() + " ]");
            } else {
                option = SelectOption.base(group.name());
            }
            options.add(option);
        }
        StateContext.PROVIDER.get().setState(GroupStates.ACTIVE_GROUPS_STATES);
        return List.of(SelectEffect.withCancelOption("Select group", options));
    }

    @Override
    public State state() {
        return CommandStates.GROUP_SETTINGS;
    }
}
