package com.dskroba.base.exception;

public class RepositoryException extends CustomException {
    public RepositoryException(String message, Exception e) {
        super(message, e);
    }

    public RepositoryException(String message) {
        super(message);
    }
}
