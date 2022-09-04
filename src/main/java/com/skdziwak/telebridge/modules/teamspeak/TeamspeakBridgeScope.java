package com.skdziwak.telebridge.modules.teamspeak;


import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import java.util.Optional;

public class TeamspeakBridgeScope {
    private static final ThreadLocal<TeamspeakBridge> bridges = new ThreadLocal<>();

    public static void runWithBridgeOptional(Optional<TeamspeakBridge> teamspeakBridge, Runnable runnable) {
        if (teamspeakBridge.isPresent()) {
            runWithBridge(teamspeakBridge.get(), runnable);
        } else {
            runnable.run();
        }
    }

    public static void runWithBridge(TeamspeakBridge bridge, Runnable runnable) {
        try {
            bridges.set(bridge);
            runnable.run();
        } finally {
            bridges.remove();
        }
    }

    public static Optional<TeamspeakBridge> getCurrentBridge() {
        return Optional.ofNullable(bridges.get());
    }
}
