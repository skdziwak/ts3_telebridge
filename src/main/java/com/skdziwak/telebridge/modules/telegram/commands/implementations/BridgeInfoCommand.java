package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.pengrad.telegrambot.request.SendMessage;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import org.springframework.beans.factory.annotation.Autowired;

@Command(name = "bridgeInfo", requiresBridge = true, permission = PermissionLevel.REGISTERED)
public class BridgeInfoCommand extends AbstractCommand {

    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        TeamspeakBridge bridge = commandContext.bridge();
        commandContext.telegramBot().execute(
                new SendMessage(commandContext.message().chat().id(), languageService.get(LanguageKey.COMMANDS_BRIDGE_INFO_RESPONSE,
                        "host", bridge.getHost(),
                        "port", String.valueOf(bridge.getPort()),
                        "queryPort", String.valueOf(bridge.getQueryPort()),
                        "username", bridge.getLogin(),
                        "owner", bridge.getOwner().getName()
                        ))
        );
    }
}
