package com.dskroba.type;

public record FileData(String filename, ContentType contentType, byte[] data) {
}
