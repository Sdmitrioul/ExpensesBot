package com.dskroba.actor;

import com.dskroba.principals.Principal;

public interface ActorManager extends AutoCloseable {
    PrincipalActor getActor(Principal principal);
}
