package com.dskroba.statemachine.events;

import com.dskroba.principals.Principal;

public sealed interface Event permits FileUploadEvent, MessageEvent, CommandEvent {
    Principal issuer();
}
