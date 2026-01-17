package com.dskroba.statemachine.handlers.group;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.base.exception.RepositoryException;
import com.dskroba.groups.Group;
import com.dskroba.groups.GroupService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandler;
import com.dskroba.statemachine.state.GroupStates;
import com.dskroba.statemachine.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.CommandStates.MENU;

@Component
public class UpdateActionHandler extends AbstractHandler {
    private final GroupService groupService;

    @Autowired
    public UpdateActionHandler(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(updateName -> switch (updateName.toLowerCase()) {
                    case "leave group" -> this.leaveGroup();
                    case "change name" -> this.changeGroupName();
                    case "add participant" -> this.addParticipant();
                    case "remove participant" -> this.removeParticipant();
                    case "delete group" -> this.deleteGroup();
                    default -> null;
                });
    }

    private List<Effect<?>> removeParticipant() {
        return null;
    }

    private List<Effect<?>> deleteGroup() {
        return null;
    }

    private List<Effect<?>> addParticipant() {
        return null;
    }

    private List<Effect<?>> changeGroupName() {
        return null;
    }

    private List<Effect<?>> leaveGroup() {
        StateContext stateContext = StateContext.PROVIDER.get();
        Group group = stateContext.getAttribute(GroupStates.GROUP_ATTRIBUTE_NAME, Group.class);
        try {
            groupService.leaveGroup(group.id(), PrincipalContext.principalId());
            return List.of(new MessageEffect("You have left group " + group.name() + "!"), MENU.initialEffect());
        } catch (RepositoryException e) {
            return List.of(new MessageEffect("There was error during group leave!"), MENU.initialEffect());
        } finally {
            stateContext.clearAttributes();
            stateContext.setState(MENU);
        }
    }

    @Override
    public State state() {
        return GroupStates.UPDATE_ACTION_CONSUMER_STATE;
    }
}
