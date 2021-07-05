package com.hoomicorp.exception;

public class UserAlreadyExistsException extends RuntimeException{

    private static final String DEFAULT_MESSAGE_TEMPLATE = "User with %s already exists";

    public UserAlreadyExistsException(final String fieldName) {
        super(String.format(DEFAULT_MESSAGE_TEMPLATE, fieldName));
    }
}
