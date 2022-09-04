package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakBridgeService;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import org.springframework.beans.factory.annotation.Autowired;

@Command(name = "unregisterBridge", requiresBridge = true, permission = PermissionLevel.BRIDGE_OWNER)
public class UnregisterBridgeCommand extends AbstractCommand {

    @Autowired
    private TeamspeakBridgeService teamspeakBridgeService;

    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        if (teamspeakBridgeService.removeBridge(commandContext.message().chat().id())) {
            commandContext.respond(languageService.get(LanguageKey.MESSAGES_OK));
        } else {
            commandContext.respond(languageService.get(LanguageKey.MESSAGES_NO_BRIDGE));
        }
    }
}
