package com.dskroba.statemachine.effects;

import com.dskroba.type.ContentType;
import com.dskroba.type.FileData;
import com.dskroba.type.FileWithMessage;

public record FileEffect(FileWithMessage payload) implements Effect {
    public static FileEffect of(String message, String filename, ContentType contentType, byte[] data) {
        return new FileEffect(new FileWithMessage(new FileData(filename, contentType, data), message));
    }
}
