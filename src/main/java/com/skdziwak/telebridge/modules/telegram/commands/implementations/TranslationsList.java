package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import org.springframework.beans.factory.annotation.Autowired;

@Command(name = "translationsList", alias = "tl", permission = PermissionLevel.BRIDGE_MODERATOR, requiresBridge = true)
public class TranslationsList extends AbstractCommand {
    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        StringBuilder stringBuilder = new StringBuilder();
        for (LanguageKey value : LanguageKey.values()) {
            stringBuilder.append(value).append(" = ").append(languageService.get(value)).append("\n");
        }
        commandContext.respond(stringBuilder.toString());
    }
}
