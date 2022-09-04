package com.skdziwak.telebridge.modules.language;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.jpa.entities.TranslationOverride;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakBridgeScope;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LanguageService {
    private final Logger logger = Logger.getGlobal();
    private final Map<String, String> translations = new HashMap<>();
    @Value("${translation.file.path:#{null}}")
    private String translationFilePath;

    @Autowired
    private TranslationOverrideRepository translationOverrideRepository;

    @PostConstruct
    private void postConstruct() {
        try (InputStream inputStream = getInputStream()) {
            translations.putAll(loadTranslations(inputStream));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private InputStream getInputStream() throws IOException {
        if (translationFilePath == null) {
            return LanguageService.class.getClassLoader().getResourceAsStream("default_language.json");
        } else {
            return Files.newInputStream(Path.of(translationFilePath));
        }
    }

    private Map<String, String> loadTranslations(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            String localeJSON = new String(inputStream.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            LanguageFile languageFile = objectMapper.readValue(localeJSON, LanguageFile.class);
            if (languageFile != null && languageFile.translations() != null) {
                return languageFile.translations();
            }
        }
        return Map.of();
    }

    private String getTranslation(LanguageKey key) {
        Optional<TeamspeakBridge> bridgeOptional = TeamspeakBridgeScope.getCurrentBridge();
        if (bridgeOptional.isPresent()) {
            TeamspeakBridge bridge = bridgeOptional.get();
            Optional<TranslationOverride> overrideOptional = translationOverrideRepository.findFirstByTeamspeakBridgeIdAndLanguageKey(bridge.getId(), key);
            if (overrideOptional.isPresent()) {
                return overrideOptional.get().getValue();
            }
        }
        return translations.get(key.name());
    }

    public void setTranslation(TeamspeakBridge bridge, LanguageKey key, String value) {
        TranslationOverride override = translationOverrideRepository.findFirstByTeamspeakBridgeIdAndLanguageKey(bridge.getId(), key)
                .orElseGet(() -> {
                    TranslationOverride newOverride = new TranslationOverride();
                    newOverride.setLanguageKey(key);
                    newOverride.setTeamspeakBridge(bridge);
                    return newOverride;
                });
        override.setValue(value);
        translationOverrideRepository.save(override);
    }

    public void resetTranslations(TeamspeakBridge bridge) {
        translationOverrideRepository.removeAllByTeamspeakBridgeId(bridge.getId());
    }

    public String get(LanguageKey key) {
        return Objects.requireNonNullElse(getTranslation(key), key.name());
    }

    public String get(LanguageKey key, Map<String, String> replacements) {
        return StringSubstitutor.replace(get(key), replacements);
    }

    public String get(LanguageKey key, String...pairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0 ; i + 1 < pairs.length ; i++) {
            map.put(pairs[i], pairs[i+1]);
        }
        return StringSubstitutor.replace(get(key), map);
    }

    private record LanguageFile(Map<String, String> translations) {

    }
}
