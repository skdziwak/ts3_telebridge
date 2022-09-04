package com.skdziwak.telebridge.modules.telegram.commands.core;

public enum PermissionLevel {
    ALL,
    REGISTERED(ALL),
    BRIDGE_MODERATOR(REGISTERED),
    BRIDGE_OWNER(REGISTERED),
    ADMIN(REGISTERED);

    private final PermissionLevel lowerLevel;


    PermissionLevel(PermissionLevel lowerLevel) {
        this.lowerLevel = lowerLevel;
    }

    PermissionLevel() {
        this.lowerLevel = null;
    }

    public PermissionLevel getLowerLevel() {
        return lowerLevel;
    }
}
