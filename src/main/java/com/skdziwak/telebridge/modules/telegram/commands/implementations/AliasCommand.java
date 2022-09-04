package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakUtils;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import org.springframework.beans.factory.annotation.Autowired;

@Command(name = "alias", permission = PermissionLevel.BRIDGE_OWNER, requiresBridge = true, arguments = {"UNIQUE_ID", "ALIAS"})
public class AliasCommand extends AbstractCommand {

    @Autowired
    private TeamspeakUtils teamspeakUtils;

    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        teamspeakUtils.setAlias(getArgument("UNIQUE_ID"), getArgument("ALIAS"));
        commandContext.respond(languageService.get(LanguageKey.MESSAGES_OK));
    }
}
