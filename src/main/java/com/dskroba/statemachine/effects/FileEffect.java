package com.dskroba.statemachine.effects;

import com.dskroba.type.FileWithMessage;

public record FileEffect(FileWithMessage payload) implements Effect<FileWithMessage> {
}
