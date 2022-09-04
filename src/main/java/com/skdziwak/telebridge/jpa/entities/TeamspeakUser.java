package com.skdziwak.telebridge.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TeamspeakUser {
    @Id
    @GeneratedValue
    private Long id;

    private String userUniqueID;

    private String alias;

    private Boolean hidden = false;

    @ManyToOne
    private TeamspeakBridge teamspeakBridge;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserUniqueID() {
        return userUniqueID;
    }

    public void setUserUniqueID(String userUniqueID) {
        this.userUniqueID = userUniqueID;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public TeamspeakBridge getTeamspeakBridge() {
        return teamspeakBridge;
    }

    public void setTeamspeakBridge(TeamspeakBridge teamspeakBridge) {
        this.teamspeakBridge = teamspeakBridge;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
