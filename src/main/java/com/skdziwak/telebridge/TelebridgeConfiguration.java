package com.skdziwak.telebridge;

import com.pengrad.telegrambot.TelegramBot;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.scope.CommandScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TelebridgeConfiguration {
    @Value("${telegram.bot.token}")
    private String telegramToken;

    @Bean
    @Scope("singleton")
    public TelegramBot telegramBot() {
        return new TelegramBot(telegramToken);
    }
}
