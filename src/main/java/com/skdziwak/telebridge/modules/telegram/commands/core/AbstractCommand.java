package com.skdziwak.telebridge.modules.telegram.commands.core;

import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakBridgeService;
import com.skdziwak.telebridge.modules.telegram.TelegramUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractCommand {
    private final Command commandAnnotation;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private CommandContext commandContext;

    public AbstractCommand() {
        assert this.getClass().isAnnotationPresent(Command.class) : "All implementations of AbstractClass should have @Command annotation.";

        commandAnnotation = this.getClass().getAnnotation(Command.class);
    }

    public abstract void command(CommandContext commandContext);

    public final String getName() {
        return commandAnnotation.name();
    }

    public final String[] getAliases() {
        return commandAnnotation.alias();
    }

    public final Optional<LanguageKey> getDescription() {
        if (commandAnnotation.description().length > 0) {
            return Optional.of(commandAnnotation.description()[0]);
        } else {
            return Optional.empty();
        }
    }

    public final String[] getArguments() {
        return commandAnnotation.arguments();
    }

    protected final String getArgument(String name) {
        String[] args = getArguments();
        int index = ArrayUtils.indexOf(args, name);
        assert index != -1 : "Invalid argument name";

        if (index < commandContext.arguments().length) {
            return commandContext.arguments()[index];
        } else {
            throw new CommandResponseException(languageService.get(LanguageKey.MESSAGES_INVALID_COMMAND));
        }
    }

    public PermissionLevel getPermissionLevel() {
        return commandAnnotation.permission();
    }

    public boolean requiresBridge() {
        return commandAnnotation.requiresBridge();
    }

    public static boolean hasPermissions(CommandContext commandContext, Class<? extends AbstractCommand> clazz, TelegramUserService telegramUserService, TeamspeakBridgeService teamspeakBridgeService) {
        Command annotation = clazz.getAnnotation(Command.class);
        if (commandContext.bridge() == null && (annotation.requiresBridge()
                || annotation.permission().equals(PermissionLevel.BRIDGE_OWNER) || annotation.permission().equals(PermissionLevel.BRIDGE_MODERATOR))) return false;
        return hasPermission(annotation.permission(), commandContext, telegramUserService, teamspeakBridgeService);
    }

    private static boolean hasPermission(PermissionLevel permissionLevel, CommandContext commandContext, TelegramUserService telegramUserService, TeamspeakBridgeService teamspeakBridgeService) {
        PermissionLevel lowerLevel = permissionLevel.getLowerLevel();
        if (lowerLevel != null && !hasPermission(lowerLevel, commandContext, telegramUserService, teamspeakBridgeService)) return false;
        switch (permissionLevel) {
            case REGISTERED -> {
                if (!telegramUserService.isUserRegistered(commandContext.message().from())) {
                    return false;
                }
            }
            case ADMIN -> {
                if (!commandContext.telegramUser().getAdmin()) {
                    return false;
                }
            }
            case BRIDGE_OWNER -> {
                if (!Objects.equals(commandContext.telegramUser().getId(), commandContext.bridge().getOwner().getId())) {
                    return false;
                }
            }
            case BRIDGE_MODERATOR -> {
                if (!Objects.equals(commandContext.telegramUser().getId(), commandContext.bridge().getOwner().getId())) { // Not an owner
                    if (!teamspeakBridgeService.isUserModerator(commandContext.bridge(), commandContext.telegramUser())) { // Not a mod
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
