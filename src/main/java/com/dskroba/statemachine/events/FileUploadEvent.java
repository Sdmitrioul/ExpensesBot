package com.dskroba.statemachine.events;

import com.dskroba.principals.Principal;
import com.dskroba.type.FileData;
import com.dskroba.type.FileWithMessage;

public record FileUploadEvent(FileWithMessage payload, Principal issuer) implements Event<FileWithMessage> {
    public static FileUploadEvent create(String message, FileData fileData, Principal issuer) {
        return new FileUploadEvent(new FileWithMessage(fileData, message), issuer);
    }
}

