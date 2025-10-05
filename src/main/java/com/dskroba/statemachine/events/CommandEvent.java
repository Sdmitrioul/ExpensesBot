package com.dskroba.statemachine.events;

import com.dskroba.principals.Principal;

public record CommandEvent(String payload, Principal issuer) implements Event<String> {
}
