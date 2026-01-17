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
import java.util.function.Supplier;

import static com.dskroba.statemachine.state.GroupStates.ACTIVATE_GROUP_STATE;
import static com.dskroba.statemachine.state.GroupStates.UPDATE_GROUP_STATE;

//TODO fix unrecognized group option
@Component
public class GroupSettingsHandler extends StateSelector {
    private static final Map<String, State> EVENT_STATE_MAP = Map.of(
            "create group", GroupStates.CREATE_GROUP_STATE);

    private final GroupService groupService;

    @Autowired
    public GroupSettingsHandler(GroupService groupService) {
        super(EVENT_STATE_MAP);
        this.groupService = groupService;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return super.handleStateEvents(event)
                .or(trySelectOption(handleMessageEvent(event), "Set active group", ACTIVATE_GROUP_STATE, true))
                .or(trySelectOption(handleMessageEvent(event), "Update group", UPDATE_GROUP_STATE, false));
    }

    private Supplier<Optional<? extends List<Effect<?>>>> trySelectOption(Optional<String> event, String command,
                                                                          State nextState, boolean showActiveGroup) {
        return () -> event
                .filter(command::equalsIgnoreCase)
                .map(_ -> groupService.getPrincipalGroups(PrincipalContext.principalId()))
                .map(groups -> createSelectGroupEffect(nextState, groups, showActiveGroup));
    }

    private List<Effect<?>> createSelectGroupEffect(State nextState, List<Group> groups, boolean showActiveGroup) {
        List<SelectOption> options = new ArrayList<>();
        for (Group group : groups) {
            if (!showActiveGroup && group.name().equals("Personal")) {
                continue;
            }
            SelectOption option;
            if (showActiveGroup && Objects.equals(group.id(), PrincipalContext.group())) {
                option = SelectOption.base("[ " + group.name() + " ]");
            } else {
                option = SelectOption.base(group.name());
            }
            options.add(option);
        }
        StateContext.PROVIDER.get().setState(nextState);
        return List.of(SelectEffect.withCancelOption("Select group", options));
    }

    @Override
    public State state() {
        return CommandStates.GROUP_SETTINGS;
    }
}
