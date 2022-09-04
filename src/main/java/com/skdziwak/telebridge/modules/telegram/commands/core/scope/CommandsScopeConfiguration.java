package com.skdziwak.telebridge.modules.telegram.commands.core.scope;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.pengrad.telegrambot.model.Message;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import java.util.Objects;

@Configuration
public class CommandsScopeConfiguration {

    @Bean
    @Scope(CommandScope.COMMAND_SCOPE)
    public CommandContext commandContext() {
        return CommandScope.getBeanStore().getCommandContext();
    }

    @Bean
    @Scope(CommandScope.COMMAND_SCOPE)
    public TS3Api ts3Api() {
        return Objects.requireNonNull(CommandScope.getBeanStore().getCommandContext().ts3Api(), "TS3 API is not available.");
    }

    @Bean
    @Scope(CommandScope.COMMAND_SCOPE)
    public Message message() {
        return CommandScope.getBeanStore().getCommandContext().message();
    }

    @Bean
    @Scope(CommandScope.COMMAND_SCOPE)
    public TeamspeakBridge teamspeakBridge() {
        return CommandScope.getBeanStore().getCommandContext().bridge();
    }

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return factory -> factory.registerScope(CommandScope.COMMAND_SCOPE, new CommandScope());
    }
}
