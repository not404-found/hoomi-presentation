package com.hoomicorp.exception;

public class ChannelNotFoundException extends RuntimeException {
    private static final String MSG_TEMPLATE = "Channel with id %s not found";

    public ChannelNotFoundException(Long id) {
        super(String.format(MSG_TEMPLATE, id));
    }

    public ChannelNotFoundException(String message) {
        super(message);
    }

    public ChannelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
