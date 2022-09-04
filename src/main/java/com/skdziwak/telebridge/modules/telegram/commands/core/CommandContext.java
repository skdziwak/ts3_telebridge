package com.skdziwak.telebridge.modules.telegram.commands.core;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.jpa.entities.TelegramUser;

public record CommandContext(Message message, TelegramBot telegramBot, String[] arguments, TelegramUser telegramUser, TeamspeakBridge bridge, TS3Api ts3Api) {
    public void respond(String message) {
        telegramBot.execute(new SendMessage(this.message.chat().id(), message));
    }
}
