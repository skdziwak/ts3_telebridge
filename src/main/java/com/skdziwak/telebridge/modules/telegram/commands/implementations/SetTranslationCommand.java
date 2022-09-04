package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.commands.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "setTranslation", arguments = {"KEY", "VALUE"}, permission = PermissionLevel.BRIDGE_MODERATOR, requiresBridge = true)
public class SetTranslationCommand extends AbstractCommand {
    private final Pattern COMMAND_PATTERN = Pattern.compile("^\\s*/(\\w+)\\s+(\\w+)\\s*(=\\s*)?((.*[\\r\\n]*)+)", Pattern.UNICODE_CHARACTER_CLASS);

    @Autowired
    private TeamspeakBridge teamspeakBridge;

    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        Matcher matcher = COMMAND_PATTERN.matcher(commandContext.message().text());
        if (matcher.find()) {
            String key = matcher.group(2);
            String value = matcher.group(4);
            LanguageKey languageKey;
            try {
                languageKey = LanguageKey.valueOf(key);
            } catch (IllegalArgumentException ex) {
                throw new CommandResponseException(languageService.get(LanguageKey.COMMANDS_SET_TRANSLATION_INVALID_KEY), ex);
            }
            languageService.setTranslation(teamspeakBridge, languageKey, value);
            commandContext.respond(languageService.get(LanguageKey.MESSAGES_OK));
        } else {
            throw new CommandResponseException(languageService.get(LanguageKey.MESSAGES_INVALID_COMMAND));
        }
    }
}
