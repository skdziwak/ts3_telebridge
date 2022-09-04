package com.skdziwak.telebridge.modules.telegram;

import com.pengrad.telegrambot.model.User;
import com.skdziwak.telebridge.jpa.entities.TelegramUser;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TelegramUserService {

    @Autowired
    private TelegramUserRepository telegramUserRepository;

    @Autowired
    private LanguageService languageService;

    public Optional<TelegramUser> getByTelegramUser(User user) {
        return telegramUserRepository.findFirstByUserId(user.id());
    }

    public TelegramUser registerUser(User user) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setUserId(user.id());
        telegramUser.setAdmin(false);
        telegramUser.setName(
                Stream.of(user.firstName(), user.username(), user.lastName())
                        .filter(Objects::nonNull).collect(Collectors.joining(" ")));
        return telegramUserRepository.saveAndFlush(telegramUser);
    }

    public void giveAdminPermissions(User user) {
        Optional<TelegramUser> optional = getByTelegramUser(user);
        if (optional.isPresent()) {
            TelegramUser telegramUser = optional.get();
            telegramUser.setAdmin(true);
            telegramUserRepository.save(telegramUser);
        } else {
            throw new CommandResponseException(languageService.get(LanguageKey.COMMANDS_MESSAGE_CLIENT_NOT_FOUND));
        }
    }

    public boolean isUserRegistered(User user) {
        return telegramUserRepository.existsByUserId(user.id());
    }
}
