package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.pengrad.telegrambot.request.SendMessage;
import com.skdziwak.telebridge.jpa.entities.TelegramUser;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.TelegramUserService;
import com.skdziwak.telebridge.modules.telegram.commands.core.*;
import org.springframework.beans.factory.annotation.Autowired;


@Command(name = "register", permission = PermissionLevel.ALL, description = LanguageKey.COMMANDS_START_DESCRIPTION)
public class RegisterCommand extends AbstractCommand {
    @Autowired
    private TelegramUserService telegramUserService;

    @Autowired
    private LanguageService languageService;

    @Override
    public void command(CommandContext commandContext) {
        if (telegramUserService.isUserRegistered(commandContext.message().from())) {
            throw new CommandResponseException(languageService.get(LanguageKey.COMMANDS_START_RESPONSE_ALREADY_REGISTERED));
        } else {
            TelegramUser telegramUser = telegramUserService.registerUser(commandContext.message().from());
            SendMessage sendMessage = new SendMessage(commandContext.message().chat().id(),
                    languageService.get(LanguageKey.COMMANDS_START_RESPONSE,
                            "user", telegramUser.getName()
                    )
            );
            commandContext.telegramBot().execute(sendMessage);
        }
    }
}
