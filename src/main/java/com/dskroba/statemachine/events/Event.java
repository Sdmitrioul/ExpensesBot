package com.dskroba.statemachine.events;

import com.dskroba.principals.Principal;

public sealed interface Event<T> permits FileUploadEvent, MessageEvent, CommandEvent {
    T payload();

    Principal issuer();
}
