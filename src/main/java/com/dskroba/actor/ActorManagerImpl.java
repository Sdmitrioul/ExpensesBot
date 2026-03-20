package com.dskroba.actor;

import com.dskroba.principals.Principal;
import com.dskroba.statemachine.AuthorizationException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ActorManagerImpl implements ActorManager {
    private final Map<Long, PrincipalActor> actors = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final ContextProvider contextProvider;

    public ActorManagerImpl(ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public PrincipalActor getActor(Principal principal) throws AuthorizationException {
        Long id = principal.key().telegramId();
        PrincipalActor actor = actors.get(id);
        if (actor == null) {
            actor = creatActor(principal);
        }
        return actor;
    }

    private PrincipalActor creatActor(Principal principal) {
        lock.lock();
        try {
            Long id = principal.key().telegramId();
            PrincipalActor actor = actors.get(id);
            if (actor == null) {
                actor = new PrincipalActorImpl(contextProvider.getContext(principal));
                actors.put(id, actor);
            }
            return actor;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        for (PrincipalActor actor : actors.values()) {
            actor.close();
        }
        actors.clear();
    }
}
