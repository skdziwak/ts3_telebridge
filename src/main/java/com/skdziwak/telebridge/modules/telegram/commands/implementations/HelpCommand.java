package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.pengrad.telegrambot.request.SendMessage;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakBridgeService;
import com.skdziwak.telebridge.modules.telegram.TelegramUserService;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.TelegramCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;

import java.util.Comparator;

@Command(name = "help", alias = "h", description = LanguageKey.COMMANDS_HELP_DESCRIPTION)
public class HelpCommand extends AbstractCommand {
    @Autowired
    private LanguageService languageService;
    @Lazy
    @Autowired
    private TelegramCommands telegramCommands;

    @Autowired
    private TeamspeakBridgeService teamspeakBridgeService;

    @Autowired
    private TelegramUserService telegramUserService;
    @Override
    public void command(CommandContext commandContext) {
        StringBuilder commandsStringBuilder = new StringBuilder();

        telegramCommands.getAllCommands().stream()
                .filter(command -> AbstractCommand.hasPermissions(commandContext, command, telegramUserService, teamspeakBridgeService))
                .map(command -> command.getAnnotation(Command.class))
                .sorted(Comparator.comparing(Command::name, String::compareTo))
                .forEach(command -> {
                    commandsStringBuilder.append(" - /").append(command.name());
                    for (String alias : command.alias()) {
                        commandsStringBuilder.append(", /").append(alias);
                    }
                    for (String argument : command.arguments()) {
                        commandsStringBuilder.append(" [").append(argument).append("]");
                    }
                    if (command.description().length > 0) {
                        commandsStringBuilder.append(" - ").append(languageService.get(command.description()[0]));
                    }
                    commandsStringBuilder.append("\n");
                });

        String response = languageService.get(LanguageKey.COMMANDS_HELP_RESPONSE,
                "commands", commandsStringBuilder.toString()
        );
        SendMessage sendMessage = new SendMessage(commandContext.message().chat().id(), response);
        commandContext.telegramBot().execute(sendMessage);
    }

}
