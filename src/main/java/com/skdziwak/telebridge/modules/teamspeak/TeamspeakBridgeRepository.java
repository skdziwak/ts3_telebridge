package com.skdziwak.telebridge.modules.teamspeak;

import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.jpa.entities.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamspeakBridgeRepository extends JpaRepository<TeamspeakBridge, Long> {
    Boolean existsByChatId(Long chatId);
    Optional<TeamspeakBridge> findByChatId(Long chatId);

    Boolean existsByIdAndModeratorsContaining(Long id, TelegramUser telegramUser);
}
