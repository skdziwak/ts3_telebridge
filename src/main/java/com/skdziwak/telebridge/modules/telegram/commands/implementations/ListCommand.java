package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakUtils;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Command(name = "list", alias = {"ls", "ll"}, permission = PermissionLevel.REGISTERED, requiresBridge = true)
public class ListCommand extends AbstractCommand {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private TS3Api ts3Api;

    @Autowired
    private TeamspeakUtils teamspeakUtils;

    @Override
    public void command(CommandContext commandContext) {
        List<Client> clients = ts3Api.getClients();

        boolean showDetails = commandContext.arguments().length == 1 && commandContext.arguments()[0].equals("-a");

        String response = getResponse(clients, languageService, teamspeakUtils, showDetails);

        commandContext.respond(response);
    }

    public static String getResponse(Collection<Client> clients, LanguageService languageService, TeamspeakUtils teamspeakUtils) {
        return getResponse(clients, languageService, teamspeakUtils, false);
    }

    public static String getResponse(Collection<Client> clients, LanguageService languageService, TeamspeakUtils teamspeakUtils, boolean showDetails) {
        String response;
        if (!showDetails) {
            clients = clients.stream().filter(Predicate.not(teamspeakUtils::isUserHidden)).collect(Collectors.toList());
        }

        if (clients.isEmpty()) {
            response = languageService.get(LanguageKey.COMMANDS_LIST_EMPTY);
        } else {
            String list = clients.stream()
                    .map(client -> teamspeakUtils.serializeClient(client, showDetails))
                    .map(s -> " - " + s)
                    .collect(Collectors.joining("\n"));
            response = languageService.get(LanguageKey.COMMANDS_LIST_RESPONSE, "list", list);
        }
        return response;
    }
}
