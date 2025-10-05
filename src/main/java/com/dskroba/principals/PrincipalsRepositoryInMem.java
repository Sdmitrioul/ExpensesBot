package com.dskroba.principals;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PrincipalsRepositoryInMem implements PrincipalRepository {
    private final AtomicLong idGenerator = new AtomicLong();
    private final Map<Long, Principal> principals = new ConcurrentHashMap<>();

    @Override
    public Principal savePrincipal(Principal principal) {
        var principalWithId = Principal.Builder.from(principal)
                .id(idGenerator.getAndIncrement())
                .build();
        return principals.merge(principalWithId.key().telegramId(), principalWithId,
                (old, newPrincipal) -> Principal.Builder.from(newPrincipal)
                        .id(old.key().id())
                        .build());
    }

    @Override
    public Principal getByTelegramId(Long telegramId) {
        return principals.get(telegramId);
    }

    @Override
    public List<Principal> getPrincipals() {
        return principals.values().stream().toList();
    }
}
