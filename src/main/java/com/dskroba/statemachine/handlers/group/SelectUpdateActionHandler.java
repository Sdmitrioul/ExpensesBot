package com.dskroba.statemachine.handlers.group;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.groups.Group;
import com.dskroba.groups.GroupService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.state.GroupStates;
import com.dskroba.statemachine.state.State;
import com.dskroba.type.SelectOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dskroba.type.SelectOption.base;

@Component
public class SelectUpdateActionHandler extends GroupSelectionHandler {
    @Autowired
    public SelectUpdateActionHandler(GroupService groupService) {
        super(groupService);
    }

    @Override
    protected List<Effect<?>> acceptGroup(Group group) {
        StateContext.PROVIDER.get().setAttribute(GroupStates.GROUP_ATTRIBUTE_NAME, group);
        List<SelectOption> options = new ArrayList<>();
        if (Objects.equals(group.owner(), PrincipalContext.principalId())) {
            options.addAll(List.of(base("Change name"), base("Add participant"),
                    base("Remove participant"), base("Delete group")));
        } else {
            options.add(base("Leave group"));
        }
        return List.of(SelectEffect.withCancelOption("Select action", options));
    }

    @Override
    public State state() {
        return GroupStates.SELECT_UPDATE_ACTION_STATE;
    }
}
