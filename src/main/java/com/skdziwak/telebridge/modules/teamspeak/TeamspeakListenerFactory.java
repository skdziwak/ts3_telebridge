package com.skdziwak.telebridge.modules.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.commands.implementations.ListCommand;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class TeamspeakListenerFactory {
    private final Logger logger = Logger.getGlobal();
    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private TeamspeakUserRepository teamspeakUserRepository;

    @Autowired
    private LanguageService languageService;

    public TeamspeakListener createListener(TS3Api ts3Api, TeamspeakBridge teamspeakBridge) {
        return new TeamspeakListener(ts3Api, teamspeakBridge);
    }

    public class TeamspeakListener extends TS3EventAdapter {
        private final TS3Api ts3Api;
        private final TeamspeakBridge teamspeakBridge;
        private final TeamspeakUtils teamspeakUtils;
        private Map<String, Client> loggedClients;

        public TeamspeakListener(TS3Api ts3Api, TeamspeakBridge teamspeakBridge) {
            this.ts3Api = ts3Api;
            this.teamspeakBridge = teamspeakBridge;
            this.teamspeakUtils = new TeamspeakUtils(ts3Api, teamspeakBridge, teamspeakUserRepository, languageService);
            this.loggedClients = getLoggedClients();
        }

        public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
            Set<Object> seen = ConcurrentHashMap.newKeySet();
            return t -> seen.add(keyExtractor.apply(t));
        }

        @NotNull
        private Map<String, Client> getLoggedClients() {
            return ts3Api.getClients().stream().filter(distinctByKey(Client::getUniqueIdentifier))
                    .collect(Collectors.toMap(Client::getUniqueIdentifier, x -> x));
        }

        private void sendMessage(String message) {
            telegramBot.execute(new SendMessage(teamspeakBridge.getChatId(), message));
        }

        private String getAlias(String uniqueID, String nickname) {
            return teamspeakUtils.getClientAliasByUniqueIdentifier(uniqueID).orElse(nickname);
        }

        private void printLoggedClients(Map<String, Client> clientsMap) {
            sendMessage(ListCommand.getResponse(clientsMap.values(), languageService, teamspeakUtils));
        }

        @Override
        public void onClientJoin(ClientJoinEvent e) {
            TeamspeakBridgeScope.runWithBridge(teamspeakBridge, () -> {
                logger.log(Level.INFO, "Client " + e.getUniqueClientIdentifier() + " (" + e.getClientNickname() + ") joined the server.");
                Boolean userHidden = teamspeakUtils.isUserHidden(e.getUniqueClientIdentifier());
                if (!userHidden) {
                    sendMessage(languageService.get(
                            LanguageKey.MESSAGES_USER_JOINED,
                            "user", getAlias(e.getUniqueClientIdentifier(), e.getClientNickname())
                    ));
                }
                this.loggedClients = getLoggedClients();
                if (!userHidden) {
                    printLoggedClients(loggedClients);
                }
            });
        }

        @Override
        public void onClientLeave(ClientLeaveEvent e) {
            TeamspeakBridgeScope.runWithBridge(teamspeakBridge, () -> {
                Map<String, Client> currentlyLoggedClients = getLoggedClients();
                AtomicReference<Boolean> printLoggedClients = new AtomicReference<>(false);
                loggedClients.forEach((uuid, client) -> {
                    logger.log(Level.INFO, "Client " + uuid + " (" + client.getNickname() + ") left the server.");
                    if (!currentlyLoggedClients.containsKey(uuid) && !teamspeakUtils.isUserHidden(uuid)) {
                        sendMessage(languageService.get(
                                LanguageKey.MESSAGES_USER_LEFT,
                                "user", getAlias(uuid, client.getNickname())
                        ));
                        printLoggedClients.set(true);
                    }
                });
                this.loggedClients = currentlyLoggedClients;
                if (printLoggedClients.get()) {
                    printLoggedClients(currentlyLoggedClients);
                }
            });
        }
    }
}
