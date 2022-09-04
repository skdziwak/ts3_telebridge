package com.skdziwak.telebridge.modules.telegram.commands.core;

public class CommandResponseException extends RuntimeException {

    public CommandResponseException(String message) {
        super(message);
    }

    public CommandResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
