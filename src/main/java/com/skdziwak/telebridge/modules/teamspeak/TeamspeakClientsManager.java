package com.skdziwak.telebridge.modules.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class TeamspeakClientsManager {
    private final Map<Long, Connector> connectorsByChat = new ConcurrentHashMap<>();
    private final ConcurrentTaskScheduler concurrentTaskScheduler = new ConcurrentTaskScheduler();

    @Autowired
    private TeamspeakListenerFactory listenerFactory;

    public void loadBridge(TeamspeakBridge teamspeakBridge) {
        assert !connectorsByChat.containsKey(teamspeakBridge.getChatId()) : "This bridge is already loaded.";

        TS3Config ts3Config = new TS3Config();
        ts3Config.setHost(teamspeakBridge.getHost());
        ts3Config.setQueryPort(teamspeakBridge.getQueryPort());
        ts3Config.setReconnectStrategy(ReconnectStrategy.linearBackoff(1000, 1000));
        ts3Config.setEnableCommunicationsLogging(true);
        ts3Config.setLoginCredentials(teamspeakBridge.getLogin(), teamspeakBridge.getPassword());
        TS3Query ts3Query = new TS3Query(ts3Config);
        ts3Query.connect();
        TS3Api api = ts3Query.getApi();
        api.selectVirtualServerByPort(teamspeakBridge.getPort());
        api.registerEvent(TS3EventType.SERVER);
        api.addTS3Listeners(listenerFactory.createListener(api, teamspeakBridge));
        concurrentTaskScheduler.schedule(api::whoAmI, new PeriodicTrigger(10, TimeUnit.SECONDS)); // Keepalive

        connectorsByChat.put(teamspeakBridge.getChatId(), new Connector(ts3Query, teamspeakBridge));
    }

    public void reloadBridge(TeamspeakBridge teamspeakBridge) {
        unloadBridge(teamspeakBridge);
        loadBridge(teamspeakBridge);
    }

    public void unloadBridge(TeamspeakBridge teamspeakBridge) {
        Connector connector = connectorsByChat.get(teamspeakBridge.getChatId());
        if (connector != null) {
            connector.ts3Query().exit();
            connectorsByChat.remove(teamspeakBridge.getChatId());
        }
    }

    public TS3Api getApiByChatID(Long chatId) {
        Connector connector = Objects.requireNonNull(connectorsByChat.get(chatId), "No bridge is registered for this chat.");
        TS3Api api = connector.ts3Query().getApi();
        api.selectVirtualServerByPort(connector.bridge().getPort());
        return api;
    }

    private record Connector(TS3Query ts3Query, TeamspeakBridge bridge) {

    }
}
