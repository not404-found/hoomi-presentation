package com.hoomicorp.exception;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException {
    private static final String MSG_TEMPLATE = "";

    public AuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public AuthenticationException(String msg) {
        super(msg);
    }
}
