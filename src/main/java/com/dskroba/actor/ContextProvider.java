package com.dskroba.actor;

import com.dskroba.principals.Principal;

public interface ContextProvider {
    Context getContext(Principal principal);
}
