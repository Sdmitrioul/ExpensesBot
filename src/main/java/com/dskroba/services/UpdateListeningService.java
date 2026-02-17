package com.dskroba.services;

import com.dskroba.actor.Context;
import com.dskroba.actor.ContextProvider;
import com.dskroba.principals.Principal;
import com.dskroba.principals.PrincipalService;
import com.dskroba.statemachine.state.StateContext;
import com.dskroba.storage.KeyValueStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateListeningService implements ContextProvider {
    private final KeyValueStorage<Long, StateContext> stateStorage;
    private final PrincipalService principalService;

    @Autowired
    public UpdateListeningService(KeyValueStorage<Long, StateContext> stateStorage,
                                  PrincipalService principalService) {
        this.stateStorage = stateStorage;
        this.principalService = principalService;
    }

    @Override
    public Context getContext(Principal principal) {
        return new Context(principal, Optional.ofNullable(stateStorage.find(principal.id()))
                .orElse(new StateContext(principal.id())),
                this::listenForPrincipal, this::listenForStateContextChange);
    }

    private void listenForPrincipal(Principal principal) {
        principalService.savePrincipal(principal);
        //TODO: add executor service
    }

    private void listenForStateContextChange(StateContext stateContext) {
        stateStorage.save(stateContext.getPrincipalId(), stateContext);
        //TODO: add executor service
    }
}
