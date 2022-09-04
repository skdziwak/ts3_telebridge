package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.pengrad.telegrambot.model.User;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakUtils;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command(name = "poke", alias = {"p"}, permission = PermissionLevel.REGISTERED, requiresBridge = true, arguments = {"NAME", "MESSAGE"})
public class PokeCommand extends AbstractCommand {
    private static final int MAX_LENGTH = 100;

    @Autowired
    private TS3Api ts3Api;

    @Autowired
    private TeamspeakUtils teamspeakUtils;

    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        Optional<Client> client = teamspeakUtils.findClientByAliasOrNickname(getArgument("NAME"));
        if (client.isPresent()) {
            String[] arguments = commandContext.arguments();
            User from = commandContext.message().from();
            String message = StringSubstitutor.replace(
                    "[${from}] ${content}",
                    Map.of(
                            "from", Stream.of(from.firstName(), from.username(), from.lastName())
                                    .filter(Objects::nonNull).collect(Collectors.joining(" ")),
                            "content", java.util.Arrays.stream(arguments, 1, arguments.length)
                                    .collect(Collectors.joining(" "))
                    )
            );
            if (message.length() > MAX_LENGTH) {
                commandContext.respond(languageService.get(LanguageKey.COMMANDS_MESSAGE_TOO_LONG));
            } else {
                ts3Api.pokeClient(client.get().getId(), message);
                commandContext.respond(languageService.get(LanguageKey.MESSAGES_OK));
            }
        } else {
            commandContext.respond(languageService.get(LanguageKey.COMMANDS_MESSAGE_CLIENT_NOT_FOUND));
        }
    }
}
