package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakUtils;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import org.springframework.beans.factory.annotation.Autowired;

@Command(name = "hide", permission = PermissionLevel.BRIDGE_OWNER, requiresBridge = true, arguments = {"UNIQUE_ID"})
public class HideCommand extends AbstractCommand {

    @Autowired
    private TeamspeakUtils teamspeakUtils;

    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        teamspeakUtils.hideUser(getArgument("UNIQUE_ID"));
        commandContext.respond(languageService.get(LanguageKey.MESSAGES_OK));
    }
}
