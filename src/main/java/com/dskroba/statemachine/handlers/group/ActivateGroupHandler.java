package com.dskroba.statemachine.handlers.group;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.groups.Group;
import com.dskroba.groups.GroupService;
import com.dskroba.principals.Principal;
import com.dskroba.principals.PrincipalService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.state.GroupStates;
import com.dskroba.statemachine.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dskroba.statemachine.state.CommandStates.MENU;

@Component
public class ActivateGroupHandler extends GroupSelectionHandler {
    private final PrincipalService principalService;

    @Autowired
    public ActivateGroupHandler(GroupService groupService, PrincipalService principalService) {
        super(groupService);
        this.principalService = principalService;
    }

    @Override
    protected List<Effect<?>> acceptGroup(Group group) {
        Principal principal = Principal.Builder.from(PrincipalContext.PROVIDER.get().getPrincipal())
                .group(group.id())
                .build();
        Principal updatedPrincipal = principalService.savePrincipal(principal);
        PrincipalContext.PROVIDER.get().setPrincipal(updatedPrincipal);
        StateContext.PROVIDER.get().setState(MENU);
        return List.of(new MessageEffect("Group " + group.name() + " is now active on your profile!"), MENU.initialEffect());
    }

    @Override
    public State state() {
        return GroupStates.ACTIVATE_GROUP_STATE;
    }
}
