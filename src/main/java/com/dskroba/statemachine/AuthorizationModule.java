package com.dskroba.statemachine;

import com.dskroba.principals.Principal;
import com.dskroba.telegram.TelegramId;

public interface AuthorizationModule {
    Principal authorize(TelegramId issuer) throws AuthorizationException;
}
