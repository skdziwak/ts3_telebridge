package com.skdziwak.telebridge.modules.teamspeak;

import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.jpa.entities.TelegramUser;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandResponseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TeamspeakBridgeService {
    private final TeamspeakBridgeRepository teamspeakBridgeRepository;
    private final TeamspeakClientsManager teamspeakClientsManager;
    private final LanguageService languageService;

    public TeamspeakBridgeService(TeamspeakBridgeRepository teamspeakBridgeRepository, TeamspeakClientsManager teamspeakClientsManager, LanguageService languageService) {
        this.teamspeakBridgeRepository = teamspeakBridgeRepository;
        this.teamspeakClientsManager = teamspeakClientsManager;
        this.languageService = languageService;
    }

    public TeamspeakBridge createBridge(TeamspeakBridge bridge) {
        TeamspeakBridge teamspeakBridge = teamspeakBridgeRepository.saveAndFlush(bridge);
        teamspeakClientsManager.loadBridge(bridge);
        return teamspeakBridge;
    }

    public boolean removeBridge(Long chatId) {
        Optional<TeamspeakBridge> optional = findByChatId(chatId);
        optional.ifPresent(teamspeakBridge -> {
            teamspeakClientsManager.unloadBridge(teamspeakBridge);
            teamspeakBridgeRepository.delete(teamspeakBridge);
        });
        return optional.isPresent();
    }

    public Boolean existsByChatId(Long chatId) {
        return teamspeakBridgeRepository.existsByChatId(chatId);
    }

    public Optional<TeamspeakBridge> findByChatId(Long chatId) {
        return teamspeakBridgeRepository.findByChatId(chatId);
    }

    public List<TeamspeakBridge> findAll() {
        return teamspeakBridgeRepository.findAll();
    }

    public Boolean isUserModerator(TeamspeakBridge teamspeakBridge, TelegramUser telegramUser) {
        return teamspeakBridgeRepository.existsByIdAndModeratorsContaining(teamspeakBridge.getId(), telegramUser);
    }

    @Transactional
    public void addModerator(Long bridgeID, TelegramUser telegramUser) {
        TeamspeakBridge bridge = teamspeakBridgeRepository.findById(bridgeID)
                .orElseThrow(() -> new CommandResponseException(languageService.get(LanguageKey.COMMANDS_CLAIM_BRIDGE_DOES_NOT_EXIST)));
        if (!bridge.getModerators().contains(telegramUser)) {
            bridge.getModerators().add(telegramUser);
            teamspeakBridgeRepository.saveAndFlush(bridge);
        }
    }
}
