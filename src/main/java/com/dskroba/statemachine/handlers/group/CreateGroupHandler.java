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
public class CreateGroupHandler extends AbstractHandler {
    private final GroupService groupService;

    @Autowired
    public CreateGroupHandler(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .flatMap(groupName -> filterIncorrectNames(groupName)
                        .or(() -> Optional.ofNullable(filterDuplicateNames(groupName)))
                        .or(() -> Optional.of(saveGroup(groupName))));
    }

    private Optional<List<Effect<?>>> filterIncorrectNames(String groupName) {
        if (groupName.isBlank()) {
            return tryAgainEffects("Group name cannot be empty!");
        }
        if (groupName.length() > 128) {
            return tryAgainEffects("Group name cannot be longer than 128 characters!");
        }
        if (!groupName.matches("^[a-zA-Z0-9-_ ]+$")) {
            return tryAgainEffects("Group name can contain only letters, digits, spaces, dashes and underscores!");
        }
        return Optional.empty();
    }

    private Optional<List<Effect<?>>> tryAgainEffects(String message) {
        return Optional.of(List.of(new MessageEffect(message), state().initialEffect()));
    }

    private List<Effect<?>> filterDuplicateNames(String groupName) {
        if (groupService.getPrincipalGroups(PrincipalContext.principalId()).stream().anyMatch(g -> g.name().equalsIgnoreCase(groupName))) {
            return List.of(new MessageEffect("Group with this name already exists!"), state().initialEffect());
        }
        return null;
    }

    private List<Effect<?>> saveGroup(String groupName) {
        try {
            groupService.createGroup(new Group.Builder().name(groupName).ownerId(PrincipalContext.principalId()).build());
            StateContext.PROVIDER.get().setState(MENU);
            return List.of(new MessageEffect("Group " + groupName + " was successfully crated!"), MENU.initialEffect());
        } catch (RepositoryException e) {
            log.error("Error while saving group", e);
            return List.of(new MessageEffect("There was error during group saving!"), state().initialEffect());
        }
    }

    @Override
    public State state() {
        return GroupStates.CREATE_GROUP_STATE;
    }
}
