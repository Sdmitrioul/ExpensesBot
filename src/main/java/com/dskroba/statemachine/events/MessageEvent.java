package com.dskroba.statemachine.events;

import com.dskroba.principals.Principal;

public record MessageEvent(String payload, Principal issuer) implements Event {
}
