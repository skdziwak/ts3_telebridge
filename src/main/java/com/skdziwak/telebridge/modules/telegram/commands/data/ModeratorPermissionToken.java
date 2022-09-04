package com.skdziwak.telebridge.modules.telegram.commands.data;

public class ModeratorPermissionToken {
    public static final String TOKEN_TYPE = "ModeratorPermissionToken";
    private Long bridgeID;

    public Long getBridgeID() {
        return bridgeID;
    }

    public void setBridgeID(Long bridgeID) {
        this.bridgeID = bridgeID;
    }
}
