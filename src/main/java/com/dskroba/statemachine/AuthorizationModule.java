package com.dskroba.statemachine;

import com.dskroba.principals.Principal;

public interface AuthorizationModule {
    Principal authorize(Principal issuer) throws AuthorizationException;
}
