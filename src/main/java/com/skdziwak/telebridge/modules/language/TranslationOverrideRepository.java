package com.skdziwak.telebridge.modules.language;

import com.skdziwak.telebridge.jpa.entities.TranslationOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

interface TranslationOverrideRepository extends JpaRepository<TranslationOverride, Integer> {
    Optional<TranslationOverride> findFirstByTeamspeakBridgeIdAndLanguageKey(Long teamspeakBridgeId, LanguageKey languageKey);
    void removeAllByTeamspeakBridgeId(Long teamspeakBridgeId);
}
