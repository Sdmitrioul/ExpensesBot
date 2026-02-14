package com.dskroba.actor;

import com.dskroba.principals.Principal;
import com.dskroba.statemachine.AuthorizationException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ActorManagerImpl implements ActorManager {
    private final Map<Long, PrincipalActor> actors = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public ActorManagerImpl(List<Principal> principals) {
        principals.forEach(this::creatActor);
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
                actor = new PrincipalActorImpl(principal);
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
