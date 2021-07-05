package com.hoomicorp.exception;

public class EventNotFoundException extends RuntimeException {

    private static final String MSG_TEMPLATE = "Event with id %s not found";


    public EventNotFoundException(Long id) {
        super(String.format(MSG_TEMPLATE, id));
    }

    public EventNotFoundException(String message) {
        super(message);
    }

    public EventNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
