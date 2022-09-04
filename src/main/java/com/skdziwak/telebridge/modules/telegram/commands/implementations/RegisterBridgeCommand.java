package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.pengrad.telegrambot.request.SendMessage;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakBridgeService;
import com.skdziwak.telebridge.modules.telegram.commands.core.*;
import org.springframework.beans.factory.annotation.Autowired;

@Command(name = "registerBridge", arguments = {"HOST", "PORT", "QUERY_PORT", "LOGIN", "PASSWORD"}, permission = PermissionLevel.ADMIN)
public class RegisterBridgeCommand extends AbstractCommand {
    @Autowired
    private LanguageService languageService;

    @Autowired
    private TeamspeakBridgeService bridgeService;

    @Override
    public void command(CommandContext commandContext) {
        String host = getArgument("HOST");
        String login = getArgument("LOGIN");
        String password = getArgument("PASSWORD");
        int port, queryPort;
        try {
            queryPort = Integer.parseInt(getArgument("QUERY_PORT"));
            port = Integer.parseInt(getArgument("PORT"));
        } catch (NumberFormatException numberFormatException) {
            throw new CommandResponseException(languageService.get(LanguageKey.MESSAGES_INVALID_COMMAND), numberFormatException);
        }

        if (bridgeService.existsByChatId(commandContext.message().chat().id())) {
            throw new CommandResponseException(languageService.get(LanguageKey.COMMANDS_REGISTER_BRIDGE_DUPLICATE));
        }

        TeamspeakBridge teamspeakBridge = new TeamspeakBridge();
        teamspeakBridge.setHost(host);
        teamspeakBridge.setLogin(login);
        teamspeakBridge.setPassword(password);
        teamspeakBridge.setPort(port);
        teamspeakBridge.setQueryPort(queryPort);
        teamspeakBridge.setChatId(commandContext.message().chat().id());
        teamspeakBridge.setOwner(commandContext.telegramUser());
        bridgeService.createBridge(teamspeakBridge);

        commandContext.telegramBot().execute(
                new SendMessage(commandContext.message().chat().id(), languageService.get(LanguageKey.COMMANDS_REGISTER_BRIDGE_SUCCESS))
        );
    }
}
