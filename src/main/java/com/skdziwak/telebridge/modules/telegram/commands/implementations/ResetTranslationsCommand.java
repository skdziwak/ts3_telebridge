package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.commands.core.*;
import org.springframework.beans.factory.annotation.Autowired;

@Command(name = "resetTranslations", permission = PermissionLevel.BRIDGE_OWNER, requiresBridge = true)
public class ResetTranslationsCommand extends AbstractCommand {
    @Autowired
    private TeamspeakBridge teamspeakBridge;

    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        languageService.resetTranslations(teamspeakBridge);
    }
}
