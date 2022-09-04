package com.skdziwak.telebridge.jpa.entities;

import com.skdziwak.telebridge.modules.language.LanguageKey;

import javax.persistence.*;

@Entity
public class TranslationOverride {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private TeamspeakBridge teamspeakBridge;

    @Enumerated(EnumType.STRING)
    private LanguageKey languageKey;

    @Column(columnDefinition = "TEXT")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamspeakBridge getTeamspeakBridge() {
        return teamspeakBridge;
    }

    public void setTeamspeakBridge(TeamspeakBridge teamspeakBridge) {
        this.teamspeakBridge = teamspeakBridge;
    }

    public LanguageKey getLanguageKey() {
        return languageKey;
    }

    public void setLanguageKey(LanguageKey languageKey) {
        this.languageKey = languageKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
