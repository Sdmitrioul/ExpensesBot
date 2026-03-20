package com.dskroba.principals;

import com.dskroba.telegram.TelegramId;

import java.util.List;

public interface PrincipalService {
    Principal savePrincipal(Principal principal);

    Principal authenticate(TelegramId issuer);

    List<Principal> getAllPrincipals();
}
