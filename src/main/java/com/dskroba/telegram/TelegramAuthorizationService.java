package com.dskroba.telegram;

import com.dskroba.principals.Principal;
import com.dskroba.principals.PrincipalService;
import com.dskroba.statemachine.AuthorizationException;
import com.dskroba.statemachine.AuthorizationModule;

import java.util.Set;

public class TelegramAuthorizationService implements AuthorizationModule {
    private final PrincipalService principalService;
    private final Set<String> allowedUsers;

    public TelegramAuthorizationService(PrincipalService principalService, Set<String> allowedUsers) {
        this.principalService = principalService;
        this.allowedUsers = allowedUsers;
    }

    @Override
    public Principal authorize(Principal issuer) throws AuthorizationException {
        if (issuer == null || (!allowedUsers.contains(issuer.code()) && !principalService.isAuthorized(issuer))) {
            throw new AuthorizationException("User is not authorized");
        }
        return principalService.authenticate(issuer);
    }
}
