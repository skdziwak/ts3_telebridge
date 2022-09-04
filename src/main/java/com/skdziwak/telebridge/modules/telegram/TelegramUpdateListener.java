package com.skdziwak.telebridge.modules.telegram;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.jpa.entities.TelegramUser;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakBridgeScope;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakBridgeService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakClientsManager;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandResponseException;
import com.skdziwak.telebridge.modules.telegram.commands.core.TelegramCommands;
import com.skdziwak.telebridge.modules.telegram.commands.core.scope.CommandScope;
import com.skdziwak.telebridge.modules.telegram.commands.implementations.AdminCommand;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TelegramUpdateListener implements ApplicationListener<ApplicationReadyEvent>, UpdatesListener {
    private static final Logger logger = Logger.getGlobal();
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\s*/(\\w+)\\s*((.*[\\r\\n]*)+)$", Pattern.UNICODE_CHARACTER_CLASS);
    private final TelegramBot telegramBot;
    private final TelegramCommands telegramCommands;
    private final LanguageService languageService;
    private final TelegramUserService telegramUserService;
    private final TeamspeakBridgeService teamspeakBridgeService;
    private final TeamspeakClientsManager clientsManager;
    @Autowired
    private ApplicationContext applicationContext;

    @Value("${telegram.debugging:#{false}}")
    private Boolean debugInfo;

    public TelegramUpdateListener(TelegramBot telegramBot, TelegramCommands telegramCommands, LanguageService languageService,
                                  TelegramUserService telegramUserService, TeamspeakBridgeService teamspeakBridgeService, TeamspeakClientsManager clientsManager) {
        this.telegramBot = telegramBot;
        this.telegramCommands = telegramCommands;
        this.languageService = languageService;
        this.telegramUserService = telegramUserService;
        this.teamspeakBridgeService = teamspeakBridgeService;
        this.clientsManager = clientsManager;
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        for (TeamspeakBridge teamspeakBridge : teamspeakBridgeService.findAll()) {
            this.clientsManager.loadBridge(teamspeakBridge);
        }
        telegramBot.setUpdatesListener(this);
        logger.log(Level.INFO, "Use this command to get admin role: /admin " + AdminCommand.ADMIN_TOKEN);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            if (update.message() != null) {
                Message message = update.message();
                if (message.text() != null) {
                    sendDebugInfo(message.chat().id(), () -> processTextMessage(message));
                }
            }
        }
        return updates.get(updates.size() - 1).updateId();
    }

    private void sendDebugInfo(Long chatId, Runnable runnable) {
        if (debugInfo) {
            long start = System.currentTimeMillis();
            try {
                runnable.run();
            } catch (RuntimeException ex) {
                telegramBot.execute(new SendMessage(chatId, ex.getMessage()));
            } finally {
                long duration = System.currentTimeMillis() - start;
                telegramBot.execute(new SendMessage(chatId, "Command processing time: " + duration + "ms"));
            }
        } else {
            runnable.run();
        }
    }

    private void processTextMessage(Message message) {
        Matcher matcher = COMMAND_PATTERN.matcher(message.text());
        if (matcher.find()) {
            String command = matcher.group(1);
            String[] arguments = matcher.group(2).strip().split("\s+");
            Optional<TeamspeakBridge> bridgeOptional = teamspeakBridgeService.findByChatId(message.chat().id());
            TeamspeakBridgeScope.runWithBridgeOptional(bridgeOptional, () -> telegramCommands.findCommand(command)
                    .ifPresentOrElse(abstractCommand -> {
                        try {
                            logger.log(Level.INFO, StringSubstitutor.replace(
                                    "Telegram user: ${user}; Executing command: ${command}", Map.of(
                                            "user", Stream.of(message.from().id(), message.from().firstName(),
                                                            message.from().lastName(), message.from().username())
                                                    .filter(Objects::nonNull).map(String::valueOf)
                                                    .collect(Collectors.joining(" ")),
                                            "command", message.text()
                                    )
                            ));
                            execute(message, arguments, abstractCommand, bridgeOptional);
                        } catch (CommandResponseException exception) {
                            logger.log(Level.WARNING, exception.getMessage(), exception);
                            SendMessage sendMessage = new SendMessage(message.chat().id(), exception.getMessage());
                            telegramBot.execute(sendMessage);
                        } catch (RuntimeException exception) {
                            logger.log(Level.SEVERE, exception.getMessage(), exception);
                            SendMessage sendMessage = new SendMessage(message.chat().id(), languageService.get(LanguageKey.MESSAGES_INTERNAL_SERVER_ERROR));
                            telegramBot.execute(sendMessage);
                        }
                    }, () -> {
                        SendMessage sendMessage = new SendMessage(message.chat().id(), languageService.get(LanguageKey.MESSAGES_INVALID_COMMAND));
                        telegramBot.execute(sendMessage);
                    }));
        }
    }

    private void execute(Message message, String[] arguments, Class<? extends AbstractCommand> commandClass, Optional<TeamspeakBridge> bridgeOptional) {
        Optional<TelegramUser> userOptional = telegramUserService.getByTelegramUser(message.from());
        TS3Api ts3Api = null;

        if (bridgeOptional.isPresent()) {
            ts3Api = clientsManager.getApiByChatID(message.chat().id());
        }

        CommandContext commandContext = new CommandContext(message, telegramBot, arguments,
                userOptional.orElse(null), bridgeOptional.orElse(null), ts3Api);
        CommandScope.runInContext(commandContext, () -> {
            validateCommandPermissions(commandClass, commandContext);
            AbstractCommand abstractCommand = applicationContext.getBean(commandClass);
            abstractCommand.command(commandContext);
        });
    }

    private void validateCommandPermissions(Class<? extends AbstractCommand> abstractCommand, CommandContext commandContext) {
        if (!AbstractCommand.hasPermissions(commandContext, abstractCommand, telegramUserService, teamspeakBridgeService)) {
            throw new CommandResponseException(languageService.get(LanguageKey.MESSAGES_FORBIDDEN));
        }
    }
}
