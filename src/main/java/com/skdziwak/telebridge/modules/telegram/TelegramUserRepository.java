package com.skdziwak.telebridge.modules.telegram;

import com.skdziwak.telebridge.jpa.entities.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findFirstByUserId(Long userId);
    Boolean existsByUserId(Long userId);
}
