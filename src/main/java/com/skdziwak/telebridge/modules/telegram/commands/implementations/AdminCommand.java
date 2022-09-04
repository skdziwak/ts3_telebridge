package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.TelegramUserService;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "admin", arguments = {"TOKEN"}, permission = PermissionLevel.REGISTERED)
public class AdminCommand extends AbstractCommand {
    private static final Logger logger = Logger.getGlobal();
    public static String ADMIN_TOKEN = randomToken();
    @Autowired
    private TelegramUserService telegramUserService;
    @Autowired
    private LanguageService languageService;

    private static String randomToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID() + UUID.randomUUID();
    }

    @Override
    public void command(CommandContext commandContext) {
        synchronized (AdminCommand.class) {
            if (ADMIN_TOKEN.equals(getArgument("TOKEN"))) {
                ADMIN_TOKEN = randomToken();
                telegramUserService.giveAdminPermissions(commandContext.message().from());
                logger.log(Level.INFO, "Use this command to get admin role: /admin " + AdminCommand.ADMIN_TOKEN);
                commandContext.respond(languageService.get(LanguageKey.MESSAGES_OK));
            }
        }
    }
}
