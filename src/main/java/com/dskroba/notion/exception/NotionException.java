package com.dskroba.notion.exception;

import com.dskroba.base.exception.CustomException;

public class NotionException extends CustomException {
    public NotionException(String message) {
        super(message);
    }

    public NotionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotionException(Throwable cause) {
        super(cause);
    }
}
