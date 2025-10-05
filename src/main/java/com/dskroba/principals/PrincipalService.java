package com.dskroba.principals;

import java.util.List;

public interface PrincipalService {
    Principal savePrincipal(Principal principal);

    Principal authenticate(Principal principal);

    boolean isAuthorized(Principal principal);

    List<Principal> getAllPrincipals();
}
